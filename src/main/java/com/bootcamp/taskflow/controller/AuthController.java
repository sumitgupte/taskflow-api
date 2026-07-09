package com.bootcamp.taskflow.controller;

import com.bootcamp.taskflow.db.InMemoryDatabase;
import com.bootcamp.taskflow.dto.ErrorResponse;
import com.bootcamp.taskflow.dto.LoginRequest;
import com.bootcamp.taskflow.dto.LoginResponse;
import com.bootcamp.taskflow.model.User;
import com.bootcamp.taskflow.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final InMemoryDatabase db;
  private final JwtService jwtService;

  public AuthController(InMemoryDatabase db, JwtService jwtService) {
    this.db = db;
    this.jwtService = jwtService;
  }

  // Simplified for the bootcamp repo: real password check is skipped.
  // Do not use this pattern outside of the lab environment.
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    User user =
        db.getUsers().stream()
            .filter(u -> u.getEmail().equals(request.getEmail()))
            .findFirst()
            .orElse(null);

    if (user == null) {
      return ResponseEntity.status(401).body(new ErrorResponse("Invalid credentials"));
    }

    String token = jwtService.issueToken(user.getId());
    LoginResponse.UserSummary summary =
        new LoginResponse.UserSummary(user.getId(), user.getName(), user.getEmail());
    return ResponseEntity.ok(new LoginResponse(token, summary));
  }
}
