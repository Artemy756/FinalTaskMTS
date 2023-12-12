package org.roombooking.repository;

import ch.qos.logback.core.joran.sanity.Pair;
import org.roombooking.entity.Auditory;
import org.roombooking.entity.BookRecord;
import org.roombooking.entity.id.AuditoryId;
import org.roombooking.entity.id.BookId;
import org.roombooking.entity.id.UserId;

import java.time.LocalDateTime;
import java.util.List;

public interface BookRecordRepository {
  BookId generateId();

  void book(BookRecord bookRecord);

  void cancelBook(BookId bookId);

  List<BookRecord> getBookRecordsForUser(UserId userId);

  List<BookRecord> getBookRecordsForAuditory(AuditoryId auditoryId);
}
