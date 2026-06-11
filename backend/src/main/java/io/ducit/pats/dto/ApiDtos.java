package io.ducit.pats.dto;

import io.ducit.pats.model.*;
import jakarta.validation.constraints.*;
import java.time.*;
import java.util.List;
import java.util.Set;

public class ApiDtos {
  public record RegisterRequest(@NotBlank String fullName, @Email @NotBlank String email, @Size(min = 6) String password) {}
  public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
  public record AuthResponse(String token, UserResponse user) {}
  public record UserResponse(Long id, String fullName, String email, Role role, boolean enabled, String phone, String avatarUrl) {
    public static UserResponse from(User user) {
      return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.isEnabled(), user.getPhone(), user.getAvatarUrl());
    }
  }
  public record ProfileRequest(@NotBlank String fullName, String phone, String avatarUrl) {}
  public record PasswordRequest(@Size(min = 6) String password) {}
  public record AdminUserRequest(@NotBlank String fullName, @Email @NotBlank String email, String password, Role role, boolean enabled) {}
  public record ScheduleRequest(Set<DayOfWeek> workDays, @NotNull LocalTime startTime, @NotNull LocalTime endTime, boolean reminderEnabled, @Min(0) int reminderMinutesBefore) {}
  public record ScheduleResponse(Set<DayOfWeek> workDays, LocalTime startTime, LocalTime endTime, boolean reminderEnabled, int reminderMinutesBefore) {
    public static ScheduleResponse from(WorkSchedule schedule) {
      return new ScheduleResponse(schedule.getWorkDays(), schedule.getStartTime(), schedule.getEndTime(), schedule.isReminderEnabled(), schedule.getReminderMinutesBefore());
    }
  }
  public record NoteRequest(String note) {}
  public record AttendanceResponse(Long id, LocalDate workDate, Instant checkInAt, Instant checkOutAt, String note, AttendanceStatus status, boolean late, long workedMinutes) {
    public static AttendanceResponse from(AttendanceRecord record) {
      long minutes = record.getCheckOutAt() == null ? 0 : Duration.between(record.getCheckInAt(), record.getCheckOutAt()).toMinutes();
      return new AttendanceResponse(record.getId(), record.getWorkDate(), record.getCheckInAt(), record.getCheckOutAt(), record.getNote(), record.getStatus(), record.isLate(), minutes);
    }
  }
  public record DashboardResponse(long workedMinutesThisWeek, long workedMinutesThisMonth, long attendanceDaysThisMonth, long lateDaysThisMonth, double lateRateThisMonth, AttendanceResponse today, List<AttendanceResponse> recent) {}
  public record AdminDashboardResponse(long totalUsers, long activeUsers, long checkedInToday, long lateToday, long workedMinutesThisMonth) {}
}
