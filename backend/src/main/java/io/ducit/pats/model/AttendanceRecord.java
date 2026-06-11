package io.ducit.pats.model;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "attendance_records")
public class AttendanceRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private User user;

  @Column(nullable = false)
  private LocalDate workDate;

  @Column(nullable = false)
  private Instant checkInAt;

  private Instant checkOutAt;

  @Column(length = 1000)
  private String note;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AttendanceStatus status = AttendanceStatus.WORKING;

  @Column(nullable = false)
  private boolean late;

  public Long getId() { return id; }
  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }
  public LocalDate getWorkDate() { return workDate; }
  public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }
  public Instant getCheckInAt() { return checkInAt; }
  public void setCheckInAt(Instant checkInAt) { this.checkInAt = checkInAt; }
  public Instant getCheckOutAt() { return checkOutAt; }
  public void setCheckOutAt(Instant checkOutAt) { this.checkOutAt = checkOutAt; }
  public String getNote() { return note; }
  public void setNote(String note) { this.note = note; }
  public AttendanceStatus getStatus() { return status; }
  public void setStatus(AttendanceStatus status) { this.status = status; }
  public boolean isLate() { return late; }
  public void setLate(boolean late) { this.late = late; }
}
