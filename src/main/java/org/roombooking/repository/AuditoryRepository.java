package org.roombooking.repository;

import org.roombooking.entity.Auditory;
import org.roombooking.entity.id.AuditoryId;

import java.sql.Time;

public interface AuditoryRepository {
  AuditoryId generateId();

  void addAuditory(Auditory auditory);

  Auditory getById(AuditoryId auditoryId);

  void update(Auditory auditory);

}
