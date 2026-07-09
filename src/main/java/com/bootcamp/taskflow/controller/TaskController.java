package com.bootcamp.taskflow.controller;

import com.bootcamp.taskflow.dto.ErrorResponse;
import com.bootcamp.taskflow.dto.TaskRequest;
import com.bootcamp.taskflow.model.Task;
import com.bootcamp.taskflow.security.AuthInterceptor;
import com.bootcamp.taskflow.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  private String currentUserId(HttpServletRequest request) {
    return (String) request.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
  }

  @GetMapping
  public List<Task> list(HttpServletRequest request, @RequestParam(required = false) String tag) {
    return taskService.getTasksForUser(currentUserId(request), tag);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getOne(HttpServletRequest request, @PathVariable String id) {
    Task task = taskService.getTaskById(currentUserId(request), id);
    if (task == null) {
      return ResponseEntity.status(404).body(new ErrorResponse("Task not found"));
    }
    return ResponseEntity.ok(task);
  }

  @PostMapping
  public ResponseEntity<?> create(HttpServletRequest request, @RequestBody TaskRequest body) {
    try {
      Task task =
          taskService.addTask(
              currentUserId(request), body.getTitle(), body.getDueDate(), body.getTags());
      return ResponseEntity.status(201).body(task);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
    }
  }

  @PatchMapping("/{id}")
  public ResponseEntity<?> update(
      HttpServletRequest request, @PathVariable String id, @RequestBody TaskRequest body) {
    Task task =
        taskService.updateTask(
            currentUserId(request),
            id,
            body.getTitle(),
            body.getDone(),
            body.getDueDate(),
            body.getTags());
    if (task == null) {
      return ResponseEntity.status(404).body(new ErrorResponse("Task not found"));
    }
    return ResponseEntity.ok(task);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable String id) {
    boolean deleted = taskService.deleteTask(currentUserId(request), id);
    if (!deleted) {
      return ResponseEntity.status(404).body(new ErrorResponse("Task not found"));
    }
    return ResponseEntity.noContent().build();
  }
}
