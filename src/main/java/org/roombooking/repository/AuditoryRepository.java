package org.roombooking.repository;

import org.roombooking.entity.Auditory;
import org.roombooking.entity.id.AuditoryId;

import java.sql.Time;

public interface AuditoryRepository {
  long generateId();

  void addAuditory(Auditory auditory);

  Auditory getById(AuditoryId auditoryId);

  boolean checkIfAvailable(Auditory auditory, Time begin, Time duration);

  void update(AuditoryId auditoryId);

}
