package br.com.urvs.servsapi.data;

import java.util.ArrayList;

import com.google.gson.Gson;

public class Groups extends ArrayList<Group> {
  public void fixDefaults() {
    for (var group : this) {
      group.fixDefaults();
    }
    this.removeIf(entry -> entry.name.isEmpty());
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public static Groups fromString(String json) {
    return new Gson().fromJson(json, Groups.class);
  }
}
