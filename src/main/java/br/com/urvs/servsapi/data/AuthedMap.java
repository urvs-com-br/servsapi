package br.com.urvs.servsapi.data;

import java.util.concurrent.ConcurrentHashMap;

public class AuthedMap extends ConcurrentHashMap<String, Authed> {
  public Authed getAuthed(String token) {
    return this.get(token);
  }

  public void addAuthed(String token, Authed authed) {
    this.put(token, authed);
  }

  public void delAuthed(String token) {
    this.remove(token);
  }
}
