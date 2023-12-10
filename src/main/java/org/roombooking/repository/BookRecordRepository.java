package org.roombooking.repository;

import ch.qos.logback.core.joran.sanity.Pair;
import org.roombooking.entity.BookRecord;
import org.roombooking.entity.id.AuditoryId;
import org.roombooking.entity.id.UserId;

import java.time.LocalDateTime;
import java.util.List;

public interface BookRecordRepository {
  long generateId();
  void book(UserId userId, AuditoryId auditoryId, Pair<LocalDateTime, LocalDateTime> time);
  boolean checkIntersection(AuditoryId auditoryId);
  List<BookRecord> getBookRecordsForUser(UserId userId);
}
