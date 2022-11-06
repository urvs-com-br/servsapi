package br.com.urvs.servsapi.swap;

import com.google.gson.Gson;

public class PathRead {
  public String path;
  public Boolean base64;
  public Integer rangeStart;
  public Integer rangeLength;

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public static PathRead fromString(String json) {
    return new Gson().fromJson(json, PathRead.class);
  }
}
