package br.com.urvs.servsapi.swap;

import com.google.gson.Gson;

public class TryAuth {
  public String name;
  public String pass;

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public static TryAuth fromString(String json) {
    return new Gson().fromJson(json, TryAuth.class);
  }
}
