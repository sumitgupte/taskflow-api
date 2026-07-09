package com.bootcamp.taskflow.model;

import java.util.ArrayList;
import java.util.List;

public class Task {

  private String id;
  private String ownerId;
  private String title;
  private boolean done;
  private String dueDate; // kept as a plain ISO string, same simplification as the JS version
  private List<String> tags = new ArrayList<>();

  public Task() {}

  public Task(
      String id, String ownerId, String title, boolean done, String dueDate, List<String> tags) {
    this.id = id;
    this.ownerId = ownerId;
    this.title = title;
    this.done = done;
    this.dueDate = dueDate;
    this.tags = tags != null ? tags : new ArrayList<>();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean isDone() {
    return done;
  }

  public void setDone(boolean done) {
    this.done = done;
  }

  public String getDueDate() {
    return dueDate;
  }

  public void setDueDate(String dueDate) {
    this.dueDate = dueDate;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags != null ? tags : new ArrayList<>();
  }
}
