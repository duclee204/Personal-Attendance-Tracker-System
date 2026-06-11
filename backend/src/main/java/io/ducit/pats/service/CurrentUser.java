package io.ducit.pats.service;

import io.ducit.pats.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
  public User get() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
