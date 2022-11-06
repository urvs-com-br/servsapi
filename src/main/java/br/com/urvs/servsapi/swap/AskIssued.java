package br.com.urvs.servsapi.swap;

import com.google.gson.Gson;

public class AskIssued {
  public String token;
  public Boolean askCreatedAt;
  public Boolean askResultLines;
  public Integer askResultLinesFrom;
  public Boolean askResultCoded;
  public Boolean askIsDone;
  public Boolean askFinishedAt;

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public static AskIssued fromString(String json) {
    return new Gson().fromJson(json, AskIssued.class);
  }
}
