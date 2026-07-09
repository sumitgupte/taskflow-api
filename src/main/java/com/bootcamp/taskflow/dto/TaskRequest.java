package com.bootcamp.taskflow.dto;

import java.util.List;

/**
 * Used for both create (POST) and partial update (PATCH). Fields are left as boxed/nullable types
 * on purpose: for PATCH, "not present in the request body" and "explicitly false/empty" need to
 * stay distinguishable. See TaskService.updateTask for how nulls are treated as "leave as-is".
 */
public class TaskRequest {

  private String title;
  private Boolean done;
  private String dueDate;
  private List<String> tags;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Boolean getDone() {
    return done;
  }

  public void setDone(Boolean done) {
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
    this.tags = tags;
  }
}
