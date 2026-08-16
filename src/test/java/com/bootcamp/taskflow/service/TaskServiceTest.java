package com.bootcamp.taskflow.service;

import static org.junit.jupiter.api.Assertions.*;

import com.bootcamp.taskflow.db.InMemoryDatabase;
import com.bootcamp.taskflow.model.Task;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Plain unit test with no Spring context - TaskService only depends on InMemoryDatabase, so there
 * is no need to boot the application to exercise this logic.
 */
class TaskServiceTest {

  private TaskService taskService;

  @BeforeEach
  void setUp() {
    InMemoryDatabase db = new InMemoryDatabase();
    taskService = new TaskService(db);
  }

  @Test
  void addTaskCreatesATaskWithTheGivenTitle() {
    Task task = taskService.addTask("u1", "Test task", null, null);
    assertEquals("Test task", task.getTitle());
    assertEquals("u1", task.getOwnerId());
    assertFalse(task.isDone());
  }

  @Test
  void getTasksForUserOnlyReturnsTasksOwnedByThatUser() {
    List<Task> tasks = taskService.getTasksForUser("u1", null);
    assertTrue(tasks.stream().allMatch(t -> t.getOwnerId().equals("u1")));
  }
}
