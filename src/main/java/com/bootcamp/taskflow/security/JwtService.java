package com.bootcamp.taskflow.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Deliberately minimal HS256 JWT implementation - just enough to issue and verify a token carrying
 * a userId and expiry, with no external dependency. This is a simplification for the lab
 * environment, same spirit as the skipped password check in AuthController. Do not lift this into a
 * real production auth system - use a maintained JWT library there.
 */
@Component
public class JwtService {

  private final SecretKeySpec key;
  private final long expiryMillis;

  public JwtService(
      @Value("${taskflow.jwt.secret}") String secret,
      @Value("${taskflow.jwt.expiry-minutes}") long expiryMinutes) {
    this.key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    this.expiryMillis = expiryMinutes * 60_000L;
  }

  public String issueToken(String userId) {
    long exp = Instant.now().toEpochMilli() + expiryMillis;
    String header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
    String payload = encode("{\"userId\":\"" + userId + "\",\"exp\":" + exp + "}");
    String signingInput = header + "." + payload;
    String signature = sign(signingInput);
    return signingInput + "." + signature;
  }

  /** Returns the userId if the token is valid and unexpired, otherwise null. */
  public String verifyAndGetUserId(String token) {
    String[] parts = token.split("\\.");
    if (parts.length != 3) {
      return null;
    }
    String signingInput = parts[0] + "." + parts[1];
    String expectedSignature = sign(signingInput);
    if (!expectedSignature.equals(parts[2])) {
      return null;
    }

    String payloadJson =
        new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
    String userId = extractJsonStringField(payloadJson, "userId");
    long exp = extractJsonLongField(payloadJson, "exp");

    if (exp < Instant.now().toEpochMilli()) {
      return null;
    }
    return userId;
  }

  private String sign(String input) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(key);
      byte[] rawSignature = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
      return Base64.getUrlEncoder().withoutPadding().encodeToString(rawSignature);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to sign JWT", e);
    }
  }

  private String encode(String json) {
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(json.getBytes(StandardCharsets.UTF_8));
  }

  // Tiny hand-written extraction - the payload shape here is fixed and
  // simple enough that pulling in a JSON library just for this felt like
  // overkill for a lab repo. A real service should use one.
  private String extractJsonStringField(String json, String field) {
    String marker = "\"" + field + "\":\"";
    int start = json.indexOf(marker);
    if (start == -1) return null;
    start += marker.length();
    int end = json.indexOf('"', start);
    return json.substring(start, end);
  }

  private long extractJsonLongField(String json, String field) {
    String marker = "\"" + field + "\":";
    int start = json.indexOf(marker);
    if (start == -1) return 0L;
    start += marker.length();
    int end = start;
    while (end < json.length() && (Character.isDigit(json.charAt(end)))) {
      end++;
    }
    return Long.parseLong(json.substring(start, end));
  }
}
