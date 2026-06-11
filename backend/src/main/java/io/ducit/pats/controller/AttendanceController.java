package io.ducit.pats.controller;

import io.ducit.pats.dto.ApiDtos.*;
import io.ducit.pats.service.*;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
  private final CurrentUser currentUser;
  private final AttendanceService attendance;

  public AttendanceController(CurrentUser currentUser, AttendanceService attendance) {
    this.currentUser = currentUser;
    this.attendance = attendance;
  }

  @PostMapping("/check-in")
  public AttendanceResponse checkIn(@RequestBody(required = false) NoteRequest request) {
    return attendance.checkIn(currentUser.get(), request == null ? null : request.note());
  }

  @PostMapping("/check-out")
  public AttendanceResponse checkOut(@RequestBody(required = false) NoteRequest request) {
    return attendance.checkOut(currentUser.get(), request == null ? null : request.note());
  }

  @PatchMapping("/{id}/note")
  public AttendanceResponse note(@PathVariable Long id, @RequestBody NoteRequest request) {
    return attendance.updateNote(currentUser.get(), id, request.note());
  }

  @GetMapping
  public List<AttendanceResponse> history(
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    return attendance.history(currentUser.get(), from, to);
  }

  @GetMapping("/dashboard")
  public DashboardResponse dashboard() {
    return attendance.dashboard(currentUser.get());
  }
}
