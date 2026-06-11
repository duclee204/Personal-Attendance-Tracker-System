package io.ducit.pats.controller;

import io.ducit.pats.dto.ApiDtos.*;
import io.ducit.pats.model.*;
import io.ducit.pats.repo.*;
import io.ducit.pats.service.AttendanceService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
  private final UserRepository users;
  private final WorkScheduleRepository schedules;
  private final AttendanceRecordRepository records;
  private final AttendanceService attendance;
  private final PasswordEncoder encoder;

  public AdminController(UserRepository users, WorkScheduleRepository schedules, AttendanceRecordRepository records, AttendanceService attendance, PasswordEncoder encoder) {
    this.users = users;
    this.schedules = schedules;
    this.records = records;
    this.attendance = attendance;
    this.encoder = encoder;
  }

  @GetMapping("/dashboard")
  public AdminDashboardResponse dashboard() {
    LocalDate today = LocalDate.now();
    LocalDate monthStart = today.withDayOfMonth(1);
    List<AttendanceResponse> month = records.findByWorkDateBetween(monthStart, today).stream().map(AttendanceResponse::from).toList();
    List<AttendanceRecord> todayRows = records.findByWorkDateBetween(today, today);
    return new AdminDashboardResponse(
      users.count(),
      users.findAll().stream().filter(User::isEnabled).count(),
      todayRows.size(),
      todayRows.stream().filter(AttendanceRecord::isLate).count(),
      AttendanceService.sumMinutes(month)
    );
  }

  @GetMapping("/users")
  public List<UserResponse> users() {
    return users.findAll().stream().map(UserResponse::from).toList();
  }

  @PostMapping("/users")
  public UserResponse create(@Valid @RequestBody AdminUserRequest request) {
    if (users.existsByEmailIgnoreCase(request.email())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    User user = new User();
    user.setFullName(request.fullName());
    user.setEmail(request.email().toLowerCase());
    user.setPasswordHash(encoder.encode(request.password() == null || request.password().isBlank() ? "123456" : request.password()));
    user.setRole(request.role() == null ? Role.USER : request.role());
    user.setEnabled(request.enabled());
    users.save(user);
    WorkSchedule schedule = new WorkSchedule();
    schedule.setUser(user);
    schedules.save(schedule);
    return UserResponse.from(user);
  }

  @PutMapping("/users/{id}")
  public UserResponse update(@PathVariable Long id, @Valid @RequestBody AdminUserRequest request) {
    User user = users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    user.setFullName(request.fullName());
    user.setEmail(request.email().toLowerCase());
    user.setRole(request.role() == null ? user.getRole() : request.role());
    user.setEnabled(request.enabled());
    if (request.password() != null && !request.password().isBlank()) user.setPasswordHash(encoder.encode(request.password()));
    return UserResponse.from(users.save(user));
  }

  @PatchMapping("/users/{id}/enabled")
  public UserResponse enabled(@PathVariable Long id, @RequestParam boolean value) {
    User user = users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    user.setEnabled(value);
    return UserResponse.from(users.save(user));
  }

  @GetMapping("/users/{id}/attendance")
  public List<AttendanceResponse> userAttendance(@PathVariable Long id) {
    User user = users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    return attendance.history(user, LocalDate.now().minusDays(30), LocalDate.now());
  }
}
