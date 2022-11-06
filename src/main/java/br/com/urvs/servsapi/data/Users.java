package br.com.urvs.servsapi.data;

import java.util.ArrayList;

import com.google.gson.Gson;

public class Users extends ArrayList<User> {
  public void fixDefaults() {
    var hasRoot = false;
    for (var user : this) {
      user.fixDefaults();
      if (user.name.equals("root")) {
        hasRoot = true;
      }
    }
    if (!hasRoot) {
      var root = new User();
      root.name = "root";
      root.pass = "14e32f44d229cdb580e90db646f87d78062b79d4";
      root.master = true;
      root.fixDefaults();
      this.add(root);
    }
    this.removeIf(entry -> entry.name.isEmpty());
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public static Users fromString(String json) {
    return new Gson().fromJson(json, Users.class);
  }
}
