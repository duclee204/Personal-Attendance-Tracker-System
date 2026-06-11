package io.ducit.pats.model;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "work_schedules")
public class WorkSchedule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional = false)
  private User user;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "work_schedule_days")
  @Enumerated(EnumType.STRING)
  private Set<DayOfWeek> workDays = new HashSet<>(Set.of(
    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
  ));

  @Column(nullable = false)
  private LocalTime startTime = LocalTime.of(8, 30);

  @Column(nullable = false)
  private LocalTime endTime = LocalTime.of(17, 30);

  @Column(nullable = false)
  private boolean reminderEnabled = true;

  @Column(nullable = false)
  private int reminderMinutesBefore = 15;

  public Long getId() { return id; }
  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }
  public Set<DayOfWeek> getWorkDays() { return workDays; }
  public void setWorkDays(Set<DayOfWeek> workDays) { this.workDays = workDays; }
  public LocalTime getStartTime() { return startTime; }
  public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
  public LocalTime getEndTime() { return endTime; }
  public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
  public boolean isReminderEnabled() { return reminderEnabled; }
  public void setReminderEnabled(boolean reminderEnabled) { this.reminderEnabled = reminderEnabled; }
  public int getReminderMinutesBefore() { return reminderMinutesBefore; }
  public void setReminderMinutesBefore(int reminderMinutesBefore) { this.reminderMinutesBefore = reminderMinutesBefore; }
}
