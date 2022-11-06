package br.com.urvs.servsapi.data;

import java.util.ArrayList;
import java.util.List;

public class Authed {
  private final User user;
  private final Group group;
  private final IssuedMap issuedMap;
  private final List<Allow> access;

  public Authed(User user, Group group) {
    this.user = user;
    this.group = group;
    this.issuedMap = new IssuedMap();
    this.access = new ArrayList<>();
    this.initAccess();
  }

  private void initAccess() {
    if (this.group != null) {
      for (var group_allow : this.group.access) {
        this.access.add(group_allow);
      }
    }
    if (this.user.access != null) {
      for (var user_allow : this.user.access) {
        this.access.removeIf(on_group -> on_group.isOnSameResource(user_allow));
        this.access.add(user_allow);
      }
    }
  }

  public String getUserName() {
    return user.name;
  }

  public String getHome() {
    if (!this.user.home.isEmpty()) {
      return this.user.home;
    } else if (this.group != null) {
      return this.group.home;
    } else {
      return "";
    }
  }

  public String getLang() {
    if (!this.user.lang.isEmpty()) {
      return this.user.lang;
    } else if (this.group != null) {
      return this.group.lang;
    } else {
      return "";
    }
  }

  public Boolean isMaster() {
    if (this.user.master) {
      return true;
    } else if (this.group != null) {
      return this.group.master;
    } else {
      return false;
    }
  }

  public List<Allow> getAccess() {
    return this.access;
  }

  public boolean allowDIR(String fullPath, boolean toMutate) {
    if (this.isMaster()) {
      return true;
    }
    for (var access : this.user.access) {
      if (access.dir != null && fullPath.startsWith(access.dir.path)) {
        if (toMutate) {
          if (access.dir.mutate) {
            return true;
          }
        } else {
          return true;
        }
      }
    }
    if (this.group != null) {
      for (var access : this.group.access) {
        if (access.dir != null && fullPath.startsWith(access.dir.path)) {
          if (toMutate) {
            if (access.dir.mutate) {
              return true;
            }
          } else {
            return true;
          }
        }
      }
    }
    return false;
  }

  public String getParam(String name) {
    if (this.user.params.containsKey(name)) {
      return this.user.params.get(name);
    }
    if (this.group != null && this.group.params.containsKey(name)) {
      return this.group.params.get(name);
    }
    return null;
  }

  public String newIssued(Issued issued) {
    return this.issuedMap.newIssued(issued);
  }

  public Issued getIssued(String token) {
    return this.issuedMap.get(token);
  }

  public void addIssued(String token, Issued issued) {
    this.issuedMap.put(token, issued);
  }

  public void delIssued(String token) {
    this.issuedMap.remove(token);
  }
}
