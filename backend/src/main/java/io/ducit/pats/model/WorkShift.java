package io.ducit.pats.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "work_shifts", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "work_date"}))
public class WorkShift {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "work_date", nullable = false)
  private LocalDate workDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ShiftType type = ShiftType.OFF;

  @Column(nullable = false)
  private LocalTime startTime = LocalTime.of(7, 0);

  @Column(nullable = false)
  private LocalTime endTime = LocalTime.of(19, 0);

  public Long getId() { return id; }
  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }
  public LocalDate getWorkDate() { return workDate; }
  public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }
  public ShiftType getType() { return type; }
  public void setType(ShiftType type) { this.type = type; }
  public LocalTime getStartTime() { return startTime; }
  public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
  public LocalTime getEndTime() { return endTime; }
  public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
}
