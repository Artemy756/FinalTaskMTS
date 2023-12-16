package org.roombooking.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.roombooking.entity.Auditory;
import org.roombooking.entity.BookRecord;
import org.roombooking.entity.User;
import org.roombooking.entity.id.AuditoryId;
import org.roombooking.entity.id.BookId;
import org.roombooking.entity.id.UserId;
import org.roombooking.repository.AuditoryRepository;
import org.roombooking.repository.BookRecordRepository;
import org.roombooking.repository.UserRepository;
import org.roombooking.service.exceptions.BookException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class BookServiceTest {


    @Test
    void book() {
        BookRecordRepository bookRecordRepository = mock(BookRecordRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        AuditoryRepository auditoryRepository = mock(AuditoryRepository.class);
        BookService bookService = new BookService(bookRecordRepository, userRepository, auditoryRepository);

        UserId userId = new UserId(12);
        UserId userId2 = new UserId(12);
        User user = new User(userId, "wqe", "1212", "wew");
        User user2 = new User(userId, "wqe", "1212", "wew");
        Mockito.when(userRepository.getUserById(userId)).thenReturn(user);
        Mockito.when(userRepository.getUserById(userId2)).thenReturn(user2);

        List<Auditory.Pair> time2 = new ArrayList<>();
        time2.add(new Auditory.Pair(LocalTime.of(11, 0, 0), LocalTime.of(15, 0, 0)));
        AuditoryId auditoryId2 = new AuditoryId(2);
        Auditory auditory2 = new Auditory(auditoryId2, "2", time2);
        Mockito.when(auditoryRepository.getAuditoryById(auditoryId2)).thenReturn(auditory2);

        Mockito.when(bookRecordRepository.generateId()).thenReturn(new BookId(1));
        LocalDateTime start = LocalDateTime.of(2023, 12, 21, 12, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 21, 13, 0, 0);

        assertEquals(bookService.book(userId, auditoryId2, start, end), new BookId(1));
        List<BookRecord> records = new ArrayList<>();
        records.add(new BookRecord(new BookId(1), userId, auditoryId2, start, end));
        Mockito.when(bookRecordRepository.getBookRecordsForAuditory(auditoryId2)).thenReturn(records);
        assertThrows(BookException.class, () -> bookService.book(userId2, auditoryId2, start, end));

        LocalDateTime start1 = LocalDateTime.of(2024, 12, 21, 12, 0, 0);
        LocalDateTime end1 = LocalDateTime.of(2024, 12, 21, 13, 0, 0);
        assertThrows(BookException.class, () -> bookService.book(userId, auditoryId2, start1, end1));

        LocalDateTime start2 = LocalDateTime.of(2020, 12, 21, 12, 0, 0);
        LocalDateTime end2 = LocalDateTime.of(2020, 12, 21, 13, 0, 0);
        assertThrows(BookException.class, () -> bookService.book(userId, auditoryId2, start2, end2));
    }
}