package com.bootcamp.taskflow.dto;

public class LoginResponse {

  private String token;
  private UserSummary user;

  public LoginResponse(String token, UserSummary user) {
    this.token = token;
    this.user = user;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public UserSummary getUser() {
    return user;
  }

  public void setUser(UserSummary user) {
    this.user = user;
  }

  public static class UserSummary {
    private String id;
    private String name;
    private String email;

    public UserSummary(String id, String name, String email) {
      this.id = id;
      this.name = name;
      this.email = email;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }
  }
}
