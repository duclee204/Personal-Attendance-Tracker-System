package io.ducit.pats.repo;

import io.ducit.pats.model.AttendanceRecord;
import io.ducit.pats.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
  Optional<AttendanceRecord> findByUserAndWorkDate(User user, LocalDate workDate);
  List<AttendanceRecord> findByUserAndWorkDateBetweenOrderByWorkDateDesc(User user, LocalDate from, LocalDate to);
  List<AttendanceRecord> findByWorkDateBetween(LocalDate from, LocalDate to);
}
