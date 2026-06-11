package io.ducit.pats.service;

import io.ducit.pats.dto.ApiDtos.*;
import io.ducit.pats.model.*;
import io.ducit.pats.repo.*;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
  private final UserRepository users;
  private final AuthTokenRepository tokens;
  private final WorkScheduleRepository schedules;
  private final PasswordEncoder encoder;

  public AuthService(UserRepository users, AuthTokenRepository tokens, WorkScheduleRepository schedules, PasswordEncoder encoder) {
    this.users = users;
    this.tokens = tokens;
    this.schedules = schedules;
    this.encoder = encoder;
  }

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (users.existsByEmailIgnoreCase(request.email())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }
    User user = new User();
    user.setFullName(request.fullName());
    user.setEmail(request.email().toLowerCase());
    user.setPasswordHash(encoder.encode(request.password()));
    user.setRole(Role.USER);
    users.save(user);
    WorkSchedule schedule = new WorkSchedule();
    schedule.setUser(user);
    schedules.save(schedule);
    return issue(user);
  }

  public AuthResponse login(LoginRequest request) {
    User user = users.findByEmailIgnoreCase(request.email())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    if (!user.isEnabled() || !encoder.matches(request.password(), user.getPasswordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    return issue(user);
  }

  public AuthResponse issue(User user) {
    AuthToken token = new AuthToken();
    token.setToken(UUID.randomUUID() + "." + UUID.randomUUID());
    token.setUser(user);
    token.setExpiresAt(Instant.now().plusSeconds(60L * 60 * 24 * 30));
    tokens.save(token);
    return new AuthResponse(token.getToken(), UserResponse.from(user));
  }
}
