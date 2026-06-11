package io.ducit.pats.controller;

import io.ducit.pats.dto.ApiDtos.*;
import io.ducit.pats.model.WorkSchedule;
import io.ducit.pats.repo.WorkScheduleRepository;
import io.ducit.pats.service.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
  private final CurrentUser currentUser;
  private final AttendanceService attendance;
  private final WorkScheduleRepository schedules;

  public ScheduleController(CurrentUser currentUser, AttendanceService attendance, WorkScheduleRepository schedules) {
    this.currentUser = currentUser;
    this.attendance = attendance;
    this.schedules = schedules;
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
}
