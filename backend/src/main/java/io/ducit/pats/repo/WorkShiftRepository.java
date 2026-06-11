package io.ducit.pats.repo;

import io.ducit.pats.model.User;
import io.ducit.pats.model.WorkShift;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkShiftRepository extends JpaRepository<WorkShift, Long> {
  Optional<WorkShift> findByUserAndWorkDate(User user, LocalDate workDate);
  List<WorkShift> findByUserAndWorkDateBetweenOrderByWorkDateAsc(User user, LocalDate from, LocalDate to);
}
