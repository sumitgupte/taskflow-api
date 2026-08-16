package com.bootcamp.taskflow.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Mirrors the Node version's requireAuth middleware: reads a Bearer token, verifies it, and stashes
 * the userId on the request for downstream controllers to read via request.getAttribute("userId").
 *
 * <p>Note: this writes its error body by hand rather than going through Jackson directly, which
 * keeps the interceptor decoupled from whichever JSON mapper the rest of the app uses.
 */
public class AuthInterceptor implements HandlerInterceptor {

  public static final String USER_ID_ATTRIBUTE = "userId";

  private final JwtService jwtService;

  public AuthInterceptor(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      writeUnauthorized(response, "Missing or malformed Authorization header");
      return false;
    }

    String token = header.substring("Bearer ".length());
    String userId = jwtService.verifyAndGetUserId(token);
    if (userId == null) {
      writeUnauthorized(response, "Invalid or expired token");
      return false;
    }

    request.setAttribute(USER_ID_ATTRIBUTE, userId);
    return true;
  }

  private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    try (PrintWriter writer = response.getWriter()) {
      writer.write("{\"error\":\"" + message + "\"}");
    }
  }
}
