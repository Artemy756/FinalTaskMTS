package org.roombooking.repository;

import org.roombooking.entity.Auditory;
import org.roombooking.entity.id.AuditoryId;

import java.util.List;


public interface AuditoryRepository {
  AuditoryId generateId();

  void addAuditory(Auditory auditory);

  List<Auditory> getAllAuditory();

  Auditory getAuditoryById(AuditoryId auditoryId);

  void update(Auditory auditory);

}
