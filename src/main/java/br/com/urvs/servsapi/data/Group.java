package br.com.urvs.servsapi.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class Group {
  public String name;
  public String home;
  public String lang;
  public Boolean master;
  public List<Allow> access;
  public Map<String, String> params;

  public void fixDefaults() {
    if (this.name == null) {
      this.name = "";
    }
    if (this.home == null) {
      this.home = "";
    }
    if (this.home.isEmpty()) {
      this.home = "dir/" + this.name;
    }
    this.home = new File(this.home).getAbsolutePath();
    if (this.lang == null) {
      this.lang = "";
    }
    if (this.master == null) {
      this.master = false;
    }
    if (this.access == null) {
      this.access = new ArrayList<>();
    }
    for (var access : this.access) {
      access.fixDefaults();
    }
    if (this.params == null) {
      this.params = new HashMap<>();
    }
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public static User fromString(String json) {
    return new Gson().fromJson(json, User.class);
  }
}
