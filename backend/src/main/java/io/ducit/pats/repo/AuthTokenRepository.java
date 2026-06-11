package io.ducit.pats.repo;

import io.ducit.pats.model.AuthToken;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthTokenRepository extends JpaRepository<AuthToken, String> {
  Optional<AuthToken> findByTokenAndExpiresAtAfter(String token, Instant now);
  void deleteByExpiresAtBefore(Instant now);
}
