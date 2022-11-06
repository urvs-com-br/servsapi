package br.com.urvs.servsapi.swap;

import com.google.gson.Gson;

public class Logged {
  public String token;
  public String lang;

  public Logged(String token, String lang) {
    this.token = token;
    this.lang = lang;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public static Logged fromString(String json) {
    return new Gson().fromJson(json, Logged.class);
  }
}
