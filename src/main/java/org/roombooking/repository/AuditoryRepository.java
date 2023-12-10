package org.roombooking.repository;

import org.roombooking.entity.Auditory;
import org.roombooking.entity.id.AuditoryId;

public interface AuditoryRepository {
  AuditoryId generateId();

  void addAuditory(Auditory auditory);

  Auditory getByAuditoryId(AuditoryId auditoryId);

  void update(Auditory auditory);

}
