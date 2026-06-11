package io.ducit.pats.service;

import io.ducit.pats.dto.ApiDtos.*;
import io.ducit.pats.model.*;
import io.ducit.pats.repo.*;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AttendanceService {
  private final AttendanceRecordRepository records;
  private final WorkScheduleRepository schedules;
  private final WorkShiftRepository shifts;

  public AttendanceService(AttendanceRecordRepository records, WorkScheduleRepository schedules, WorkShiftRepository shifts) {
    this.records = records;
    this.schedules = schedules;
    this.shifts = shifts;
  }

  @Transactional
  public AttendanceResponse checkIn(User user, String note) {
    LocalDate today = LocalDate.now();
    if (records.findByUserAndWorkDate(user, today).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Already checked in today");
    }
    WorkSchedule schedule = getSchedule(user);
    WorkShift shift = shifts.findByUserAndWorkDate(user, today).orElse(null);
    AttendanceRecord record = new AttendanceRecord();
    record.setUser(user);
    record.setWorkDate(today);
    record.setCheckInAt(Instant.now());
    record.setNote(note);
    LocalTime expectedStart = shift != null && shift.getType() != ShiftType.OFF ? shift.getStartTime() : schedule.getStartTime();
    record.setLate(LocalTime.now().isAfter(expectedStart));
    return AttendanceResponse.from(records.save(record));
  }

  @Transactional
  public AttendanceResponse checkOut(User user, String note) {
    AttendanceRecord record = records.findByUserAndWorkDate(user, LocalDate.now())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No check-in for today"));
    if (record.getCheckOutAt() != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Already checked out today");
    }
    record.setCheckOutAt(Instant.now());
    record.setStatus(AttendanceStatus.COMPLETED);
    if (note != null && !note.isBlank()) record.setNote(note);
    return AttendanceResponse.from(record);
  }

  @Transactional
  public AttendanceResponse updateNote(User user, Long id, String note) {
    AttendanceRecord record = records.findById(id)
      .filter(item -> item.getUser().getId().equals(user.getId()))
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance record not found"));
    record.setNote(note);
    return AttendanceResponse.from(record);
  }

  @Transactional
  public AttendanceResponse update(User user, Long id, AttendanceUpdateRequest request) {
    AttendanceRecord record = records.findById(id)
      .filter(item -> item.getUser().getId().equals(user.getId()))
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance record not found"));
    if (request.checkOutAt() != null && request.checkOutAt().isBefore(request.checkInAt())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check-out must be after check-in");
    }
    record.setWorkDate(request.workDate());
    record.setCheckInAt(request.checkInAt());
    record.setCheckOutAt(request.checkOutAt());
    record.setNote(request.note());
    record.setLate(request.late());
    record.setStatus(request.checkOutAt() == null ? AttendanceStatus.WORKING : AttendanceStatus.COMPLETED);
    return AttendanceResponse.from(record);
  }

  public List<AttendanceResponse> history(User user, LocalDate from, LocalDate to) {
    LocalDate end = to == null ? LocalDate.now() : to;
    LocalDate start = from == null ? end.minusDays(30) : from;
    return records.findByUserAndWorkDateBetweenOrderByWorkDateDesc(user, start, end).stream().map(AttendanceResponse::from).toList();
  }

  public DashboardResponse dashboard(User user) {
    LocalDate today = LocalDate.now();
    LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    LocalDate monthStart = today.withDayOfMonth(1);
    List<AttendanceResponse> week = history(user, weekStart, today);
    List<AttendanceResponse> month = history(user, monthStart, today);
    List<AttendanceResponse> recent = history(user, today.minusDays(7), today);
    long monthDays = month.size();
    long lateDays = month.stream().filter(AttendanceResponse::late).count();
    AttendanceResponse todayRecord = records.findByUserAndWorkDate(user, today).map(AttendanceResponse::from).orElse(null);
    List<ShiftResponse> upcoming = shifts.findByUserAndWorkDateBetweenOrderByWorkDateAsc(user, today, today.plusDays(6)).stream().map(ShiftResponse::from).toList();
    return new DashboardResponse(sumMinutes(week), sumMinutes(month), monthDays, lateDays, monthDays == 0 ? 0 : (lateDays * 100.0 / monthDays), todayRecord, recent, upcoming);
  }

  public WorkSchedule getSchedule(User user) {
    return schedules.findByUser(user).orElseGet(() -> {
      WorkSchedule schedule = new WorkSchedule();
      schedule.setUser(user);
      return schedules.save(schedule);
    });
  }

  public static long sumMinutes(List<AttendanceResponse> rows) {
    return rows.stream().mapToLong(AttendanceResponse::workedMinutes).sum();
  }
}
