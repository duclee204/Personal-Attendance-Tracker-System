package io.ducit.pats.controller;

import io.ducit.pats.dto.ApiDtos.*;
import io.ducit.pats.model.*;
import io.ducit.pats.repo.*;
import io.ducit.pats.service.*;
import jakarta.validation.Valid;
import java.time.*;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
  private final CurrentUser currentUser;
  private final AttendanceService attendance;
  private final WorkScheduleRepository schedules;
  private final WorkShiftRepository shifts;

  public ScheduleController(CurrentUser currentUser, AttendanceService attendance, WorkScheduleRepository schedules, WorkShiftRepository shifts) {
    this.currentUser = currentUser;
    this.attendance = attendance;
    this.schedules = schedules;
    this.shifts = shifts;
  }

  @GetMapping
  public ScheduleResponse get() {
    return ScheduleResponse.from(attendance.getSchedule(currentUser.get()));
  }

  @PutMapping
  public ScheduleResponse update(@Valid @RequestBody ScheduleRequest request) {
    WorkSchedule schedule = attendance.getSchedule(currentUser.get());
    if (request.workDays() != null && !request.workDays().isEmpty()) schedule.setWorkDays(request.workDays());
    schedule.setStartTime(request.startTime());
    schedule.setEndTime(request.endTime());
    schedule.setReminderEnabled(request.reminderEnabled());
    schedule.setReminderMinutesBefore(request.reminderMinutesBefore());
    return ScheduleResponse.from(schedules.save(schedule));
  }

  @GetMapping("/shifts")
  public List<ShiftResponse> month(@RequestParam String month) {
    YearMonth selectedMonth = YearMonth.parse(month);
    LocalDate start = selectedMonth.atDay(1);
    LocalDate end = selectedMonth.atEndOfMonth();
    return shifts.findByUserAndWorkDateBetweenOrderByWorkDateAsc(currentUser.get(), start, end).stream().map(ShiftResponse::from).toList();
  }

  @PutMapping("/shifts")
  public List<ShiftResponse> updateShifts(@Valid @RequestBody List<ShiftRequest> requests) {
    User user = currentUser.get();
    for (ShiftRequest request : requests) {
      WorkShift shift = shifts.findByUserAndWorkDate(user, request.workDate()).orElseGet(() -> {
        WorkShift created = new WorkShift();
        created.setUser(user);
        created.setWorkDate(request.workDate());
        return created;
      });
      shift.setType(request.type());
      shift.setStartTime(request.startTime());
      shift.setEndTime(request.endTime());
      shifts.save(shift);
    }
    if (requests.isEmpty()) return List.of();
    YearMonth month = YearMonth.from(requests.get(0).workDate());
    return month(month.toString());
  }

  @GetMapping("/upcoming")
  public List<ShiftResponse> upcoming(@RequestParam(defaultValue = "7") int days) {
    LocalDate today = LocalDate.now();
    return shifts.findByUserAndWorkDateBetweenOrderByWorkDateAsc(currentUser.get(), today, today.plusDays(Math.max(1, days) - 1)).stream().map(ShiftResponse::from).toList();
  }
}
