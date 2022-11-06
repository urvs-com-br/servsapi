package br.com.urvs.servsapi.swap;

import com.google.gson.Gson;

public class OnePath {
  public String path;

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public static OnePath fromString(String json) {
    return new Gson().fromJson(json, OnePath.class);
  }
}
