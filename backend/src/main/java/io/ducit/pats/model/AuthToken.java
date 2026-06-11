package io.ducit.pats.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "auth_tokens")
public class AuthToken {
  @Id
  @Column(length = 80)
  private String token;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private User user;

  @Column(nullable = false)
  private Instant expiresAt;

  public String getToken() { return token; }
  public void setToken(String token) { this.token = token; }
  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }
  public Instant getExpiresAt() { return expiresAt; }
  public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
