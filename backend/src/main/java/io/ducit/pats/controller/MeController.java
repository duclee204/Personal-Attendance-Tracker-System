package io.ducit.pats.controller;

import io.ducit.pats.dto.ApiDtos.*;
import io.ducit.pats.model.User;
import io.ducit.pats.repo.UserRepository;
import io.ducit.pats.service.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
public class MeController {
  private final CurrentUser currentUser;
  private final UserRepository users;
  private final PasswordEncoder encoder;

  public MeController(CurrentUser currentUser, UserRepository users, PasswordEncoder encoder) {
    this.currentUser = currentUser;
    this.users = users;
    this.encoder = encoder;
  }

  @GetMapping
  public UserResponse me() {
    return UserResponse.from(currentUser.get());
  }

  @PutMapping
  public UserResponse update(@Valid @RequestBody ProfileRequest request) {
    User user = currentUser.get();
    user.setFullName(request.fullName());
    user.setPhone(request.phone());
    user.setAvatarUrl(request.avatarUrl());
    return UserResponse.from(users.save(user));
  }

  @PutMapping("/password")
  public void password(@Valid @RequestBody PasswordRequest request) {
    User user = currentUser.get();
    user.setPasswordHash(encoder.encode(request.password()));
    users.save(user);
  }
}
