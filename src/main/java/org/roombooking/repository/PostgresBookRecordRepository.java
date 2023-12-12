package org.roombooking.repository;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.roombooking.entity.BookRecord;
import org.roombooking.entity.id.AuditoryId;
import org.roombooking.entity.id.BookId;
import org.roombooking.entity.id.UserId;
import org.roombooking.repository.exceptions.ItemNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PostgresBookRecordRepository implements BookRecordRepository {

    private final Jdbi jdbi;

    public PostgresBookRecordRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public BookId generateId() {
        try (Handle handle = jdbi.open()) {
            long value = (long) handle.createQuery(
                            "SELECT nextval('books_book_id_seq') AS value")
                    .mapToMap()
                    .first()
                    .get("value");
            return new BookId(value);
        }
    }

    @Override
    public void book(BookRecord bookRecord) {
        jdbi.useTransaction((Handle handle) -> handle.createUpdate(
                        "INSERT INTO books (book_id, user_id, auditory_id, start_time, end_time) VALUES (:bookId, :userId, :auditoryId, :startTime, :endTime)")
                .bind("bookId", bookRecord.bookId())
                .bind("userId", bookRecord.userId())
                .bind("auditoryId", bookRecord.auditoryId())
                .bind("start_time", bookRecord.startTime())
                .bind("end_time", bookRecord.endTime())
                .execute());
    }

    @Override
    public void cancelBook(BookId bookId) {
        jdbi.useTransaction((Handle handle) -> {
            int deleted = handle.createUpdate(
                            "DELETE FROM books WHERE book_id = :bookId")
                    .bind("bookId", bookId.value())
                    .execute();

            if (deleted == 0) {
                throw new ItemNotFoundException("Couldn't find book record with id=" + bookId);
            }
        });
    }

    @Override
    public List<BookRecord> getBookRecordsForUser(UserId userId) {
        try {
            return jdbi.inTransaction((Handle handle) -> handle.createQuery(
                            "SELECT book_id, user_id, auditory_id, start_time, end_time FROM books WHERE user_id = :userId")
                    .bind("userId", userId.value())
                    .mapToMap()
                    .list()
                    .stream()
                    .map((Map<String, Object> result) -> new BookRecord(
                            new BookId((long) result.get("book_id")),
                            new UserId((long) result.get("user_id")),
                            new AuditoryId((long) result.get("auditory_id")),
                            (LocalDateTime) result.get("start_time"),
                            (LocalDateTime) result.get("end_time")))
                    .collect(Collectors.toList())
            );
        } catch(NullPointerException e) {
            throw new ItemNotFoundException("Couldn't retrieve any book records for userId=" + userId);
        }
    }

    @Override
    public List<BookRecord> getBookRecordsForAuditory(AuditoryId auditoryId) {
        try {
            return jdbi.inTransaction((Handle handle) -> handle.createQuery(
                            "SELECT book_id, user_id, auditory_id, start_time, end_time FROM books WHERE auditory_id = :auditoryId")
                    .bind("auditoryId", auditoryId.value())
                    .mapToMap()
                    .list()
                    .stream()
                    .map((Map<String, Object> result) -> new BookRecord(
                            new BookId((long) result.get("book_id")),
                            new UserId((long) result.get("user_id")),
                            new AuditoryId((long) result.get("auditory_id")),
                            (LocalDateTime) result.get("start_time"),
                            (LocalDateTime) result.get("end_time")))
                    .collect(Collectors.toList())
            );
        } catch(NullPointerException e) {
            throw new ItemNotFoundException("Couldn't retrieve any book records for auditoryId=" + auditoryId);
        }
    }
}
