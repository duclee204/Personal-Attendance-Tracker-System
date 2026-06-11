package io.ducit.pats.repo;

import io.ducit.pats.model.User;
import io.ducit.pats.model.WorkSchedule;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
  Optional<WorkSchedule> findByUser(User user);
}
