package org.roombooking.entity;

import org.roombooking.entity.id.AuditoryId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Auditory {
  private final AuditoryId auditoryId;
  private final String number;
  private final List<LocalDateTime> availableTime;

  public Auditory(AuditoryId auditoryId, String number, List<LocalDateTime> availableTime) {
    this.auditoryId = auditoryId;
    this.number = number;
    this.availableTime = availableTime;
  }

  public Auditory withNumber(String newNumber) {
    return new Auditory(this.auditoryId, newNumber, this.availableTime);
  }

  public Auditory withAvailableTime(List<LocalDateTime> newTime) {
    return new Auditory(this.auditoryId, this.number, newTime);
  }

  public AuditoryId getId() {
    return auditoryId;
  }

  public String getNumber() {
    return number;
  }

  public List<LocalDateTime> getAvailableTime() {
    return availableTime;
  }

  @Override
  public String toString() {
    return "Auditory{" +
            "auditoryId=" + auditoryId +
            ", number='" + number + '\'' +
            ", availableTime=" + availableTime +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Auditory auditory = (Auditory) o;
    return Objects.equals(auditoryId, auditory.auditoryId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(auditoryId);
  }
}
