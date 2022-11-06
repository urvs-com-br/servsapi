package br.com.urvs.servsapi.data;

import java.io.File;
import java.util.Objects;

import com.google.gson.Gson;

public class Allow {
  public DIR dir;

  public static class DIR {
    public String path;
    public Boolean mutate;
  }

  public void fixDefaults() {
    if (this.dir != null) {
      if (this.dir.path == null || this.dir.path.isEmpty()) {
        this.dir = null;
      } else {
        this.dir.path = new File(this.dir.path).getAbsolutePath();
        this.dir.mutate = this.dir.mutate != null ? this.dir.mutate : false;
      }
    }
  }

  public boolean isOnSameResource(Allow than) {
    if (this.dir != null && than.dir != null) {
      return Objects.equals(this.dir.path, than.dir.path);
    }
    return false;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public static Allow fromString(String json) {
    return new Gson().fromJson(json, Allow.class);
  }
}
