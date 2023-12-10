package org.roombooking.repository;

import org.roombooking.entity.Auditory;
import org.roombooking.entity.id.AuditoryId;


public interface AuditoryRepository {
  AuditoryId generateId();

  void addAuditory(Auditory auditory);

  List<Auditory> getAllAuditory();

  Auditory getAuditoryById(AuditoryId auditoryId);

  boolean checkIfAvailable(Auditory auditory, Time begin, Time duration);

  void update(Auditory auditory);

}
