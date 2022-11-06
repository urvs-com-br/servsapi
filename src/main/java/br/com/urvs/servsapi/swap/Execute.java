package br.com.urvs.servsapi.swap;

import java.util.List;

import com.google.gson.Gson;

public class Execute {
  public String exec;
  public List<String> args;
  public List<String> inputs;

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public static Execute fromString(String json) {
    return new Gson().fromJson(json, Execute.class);
  }
}
