package com.bootcamp.taskflow.model;

public class User {

  private String id;
  private String email;
  private String passwordHash; // unused in this demo build - see AuthController
  private String name;

  public User() {}

  public User(String id, String email, String passwordHash, String name) {
    this.id = id;
    this.email = email;
    this.passwordHash = passwordHash;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
