package br.com.urvs.servsapi.swap;

import com.google.gson.Gson;

public class TwoPath {
  public String origin;
  public String destiny;

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public static TwoPath fromString(String json) {
    return new Gson().fromJson(json, TwoPath.class);
  }
}
