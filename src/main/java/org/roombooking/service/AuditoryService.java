package org.roombooking.service;

import org.roombooking.entity.Auditory;
import org.roombooking.entity.id.AuditoryId;
import org.roombooking.repository.AuditoryRepository;
import org.roombooking.service.exceptions.AuditoryCreateException;
import org.roombooking.service.exceptions.AuditoryNotFoundException;
import org.roombooking.service.exceptions.AuditoryUpdateException;

import java.time.LocalDateTime;
import java.util.List;

public class AuditoryService {
  private final AuditoryRepository auditoryRepository;

  public AuditoryService(AuditoryRepository auditoryRepository) {
    this.auditoryRepository = auditoryRepository;
  }

  public List<Auditory> getAllAuditory() {
    return auditoryRepository.getAllAuditory();
  }

  public Auditory getAuditoryById(AuditoryId id) {
    try {
      return auditoryRepository.getAuditoryById(id);
    } catch (AuditoryNotFoundException e) {
      throw new AuditoryNotFoundException("Cannot find auditory with id: " + id, e);
    }
  }

  public AuditoryId addAuditory(String number, List<LocalDateTime> availableTime) {
    AuditoryId auditoryId = auditoryRepository.generateId();
    Auditory auditory = new Auditory(auditoryId, number, availableTime);
    try {
      auditoryRepository.addAuditory(auditory);
    } catch (RuntimeException e) {
      throw new AuditoryCreateException("Cannot create auditory", e);
    }
    return auditoryId;
  }

  public void updateAuditoryName(AuditoryId id, String number) {
    Auditory auditory;
    try {
      auditory = auditoryRepository.getAuditoryById(id);
    } catch (AuditoryNotFoundException e) {
      throw new AuditoryUpdateException("Cannot update auditory with id: " + id, e);
    }
    try {
      auditoryRepository.update(auditory.withNumber(number));
    } catch (AuditoryNotFoundException e) {
      throw new AuditoryUpdateException("Cannot update auditory with id: " + id, e);
    }
  }

  public void updateAuditoryTime(AuditoryId id, List<LocalDateTime> availableTime) {
    Auditory auditory;
    try {
      auditory = auditoryRepository.getAuditoryById(id);
    } catch (AuditoryNotFoundException e) {
      throw new AuditoryUpdateException("Cannot update auditory with id: " + id, e);
    }
    try {
      auditoryRepository.update(auditory.withAvailableTime(availableTime));
    } catch (AuditoryNotFoundException e) {
      throw new AuditoryUpdateException("Cannot update auditory with id: " + id, e);
    }
  }
}
