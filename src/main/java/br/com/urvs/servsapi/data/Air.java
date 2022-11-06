package br.com.urvs.servsapi.data;

public class Air {
  public final Setup setup;
  public final Users users;
  public final Groups groups;

  public Air(Setup setup, Users users, Groups groups) throws Exception {
    this.setup = setup;
    this.users = users;
    this.groups = groups;
  }
}