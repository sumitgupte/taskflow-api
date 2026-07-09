package com.bootcamp.taskflow.service;

import static org.junit.jupiter.api.Assertions.*;

import com.bootcamp.taskflow.db.InMemoryDatabase;
import com.bootcamp.taskflow.model.Task;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * NOTE: Coverage here is thin on purpose. There are no tests for: - filtering by tag - updateTask -
 * deleteTask - addTask validation (missing title) Leave gaps as-is; they're useful for the
 * "Implement" lab, where participants generate tests for a plan step using Copilot.
 *
 * <p>This is a plain unit test with no Spring context - TaskService only depends on
 * InMemoryDatabase, so there's no need to boot the application just to exercise this logic.
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
