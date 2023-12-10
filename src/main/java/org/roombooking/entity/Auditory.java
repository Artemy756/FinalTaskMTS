package org.roombooking.entity;

import org.roombooking.entity.id.AuditoryId;

import java.time.LocalDateTime;
import java.util.List;

public class Auditory {
  private AuditoryId auditoryId;
  private String number;
  private List<LocalDateTime> availableTime;

  public Auditory(AuditoryId auditoryId, String number, List<LocalDateTime> availableTime) {
    this.auditoryId = auditoryId;
    this.number = number;
    this.availableTime = availableTime;
  }
}
