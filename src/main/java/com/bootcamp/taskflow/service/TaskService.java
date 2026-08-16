package com.bootcamp.taskflow.service;

import com.bootcamp.taskflow.db.InMemoryDatabase;
import com.bootcamp.taskflow.model.Task;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

  private final InMemoryDatabase db;

  public TaskService(InMemoryDatabase db) {
    this.db = db;
  }

  public List<Task> getTasksForUser(String userId, String tag) {
    List<Task> result = new ArrayList<>();
    for (Task task : db.getTasks()) {
      if (task.getOwnerId().equals(userId)) {
        result.add(task);
      }
    }
    if (tag != null && !tag.isBlank()) {
      result.removeIf(task -> !task.getTags().contains(tag));
    }
    result.sort(Comparator.comparing(t -> t.getDueDate() == null ? "" : t.getDueDate()));
    return result;
  }

  public Task getTaskById(String userId, String taskId) {
    for (Task task : db.getTasks()) {
      if (task.getId().equals(taskId) && task.getOwnerId().equals(userId)) {
        return task;
      }
    }
    return null;
  }

  public Task addTask(String userId, String title, String dueDate, List<String> tags) {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("title is required");
    }
    Task task = new Task(UUID.randomUUID().toString(), userId, title, false, dueDate, tags);
    db.getTasks().add(task);
    return task;
  }

  /**
   * Applies only the non-null fields present in the update - mirrors the Node version's
   * Object.assign(task, updates) behavior, where fields absent from the request body are left
   * untouched.
   */
  public Task updateTask(
      String userId, String taskId, String title, Boolean done, String dueDate, List<String> tags) {
    Task task = getTaskById(userId, taskId);
    if (task == null) {
      return null;
    }
    if (title != null) task.setTitle(title);
    if (done != null) task.setDone(done);
    if (dueDate != null) task.setDueDate(dueDate);
    if (tags != null) task.setTags(tags);
    return task;
  }

  public boolean deleteTask(String userId, String taskId) {
    return db.getTasks()
        .removeIf(t -> Objects.equals(t.getId(), taskId) && Objects.equals(t.getOwnerId(), userId));
  }
}
