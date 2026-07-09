package com.bootcamp.taskflow.db;

import com.bootcamp.taskflow.model.Task;
import com.bootcamp.taskflow.model.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * A very small in-memory "database" so the bootcamp repo runs with zero external setup - no
 * Postgres/MySQL, no Docker. Not a pattern to copy for production use.
 */
@Component
public class InMemoryDatabase {

  private final List<User> users = new ArrayList<>();
  private final List<Task> tasks = new ArrayList<>();

  public InMemoryDatabase() {
    users.add(
        new User(
            "u1",
            "alice@example.com",
            "$2a$10$CwTycUXWue0Thq9StjUM0uJ8Q4z0k7XyJZ2z2Z2Z2Z2Z2Z2Z2Z2Zu",
            "Alice Johnson"));
    users.add(
        new User(
            "u2",
            "bob@example.com",
            "$2a$10$CwTycUXWue0Thq9StjUM0uJ8Q4z0k7XyJZ2z2Z2Z2Z2Z2Z2Z2Z2Zu",
            "Bob Martinez"));

    tasks.add(new Task("t1", "u1", "Write Q3 report", false, "2026-07-15", List.of("work")));
    tasks.add(
        new Task("t2", "u1", "Book dentist appointment", false, "2026-07-10", List.of("personal")));
    tasks.add(
        new Task("t3", "u2", "Review PR #482", true, "2026-07-05", List.of("work", "urgent")));
    tasks.add(new Task("t4", "u2", "Plan team offsite", false, "2026-08-01", List.of("work")));
  }

  public List<User> getUsers() {
    return users;
  }

  public List<Task> getTasks() {
    return tasks;
  }
}
