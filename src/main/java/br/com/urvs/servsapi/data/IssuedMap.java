package br.com.urvs.servsapi.data;

import java.util.HashMap;

import br.com.urvs.servsapi.work.Utils;

public class IssuedMap extends HashMap<String, Issued> {
  public String newIssued(Issued issued) {
    String token = Utils.newRandomToken();
    this.put(token, issued);
    return token;
  }

  public Issued getIssued(String token) {
    return this.get(token);
  }

  public void addIssued(String token, Issued issued) {
    this.put(token, issued);
  }

  public void delIssued(String token) {
    this.remove(token);
  }
}
