package org.roombooking.service;

import org.roombooking.entity.Auditory;
import org.roombooking.entity.BookRecord;
import org.roombooking.entity.User;
import org.roombooking.entity.id.AuditoryId;
import org.roombooking.entity.id.BookId;
import org.roombooking.entity.id.UserId;
import org.roombooking.repository.AuditoryRepository;
import org.roombooking.repository.BookRecordRepository;
import org.roombooking.repository.UserRepository;
import org.roombooking.repository.exceptions.ItemNotFoundException;
import org.roombooking.service.exceptions.AuditoryNotFoundException;
import org.roombooking.service.exceptions.BookException;
import org.roombooking.service.exceptions.UserNotFoundException;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class BookService {

    private final BookRecordRepository bookRecordRepository;
    private final UserRepository userRepository;
    private final AuditoryRepository auditoryRepository;
    private final Duration WEEKLY_LIMIT = Duration.ofHours(3);
    private final Duration PREMIUM_LIMIT = Duration.ofHours(6);

    public BookService(BookRecordRepository bookRecordRepository, UserRepository userRepository, AuditoryRepository auditoryRepository) {
        this.bookRecordRepository = bookRecordRepository;
        this.userRepository = userRepository;
        this.auditoryRepository = auditoryRepository;
    }

    private boolean verifyInterval(Auditory auditory, LocalDateTime start, LocalDateTime end) {
        LocalTime userBegin = start.toLocalTime();
        LocalTime userEnd = end.toLocalTime();

        if (userEnd.isAfter(userBegin)) {
            return false;
        }

        //check that interval is round to 5 min

        if (!(start.isAfter(LocalDateTime.now()))) {
            return false;
        }

        if (!(end.isBefore(LocalDateTime.now().plusWeeks(2)))) {
            return false;
        }

        for (Auditory.Pair pair : auditory.getAvailableTime()) {
            LocalTime auditoryBegin = pair.begin();
            LocalTime auditoryEnd = pair.end();
            if ((auditoryBegin.equals(userBegin) || auditoryBegin.isBefore(userBegin)) && (auditoryEnd.equals(userEnd) || auditoryEnd.isAfter(userEnd))) {
                return true;
            }
        }
        return false;
    }

    private Duration getAllowedDuration(User user) {
        List<BookRecord> books = bookRecordRepository.getBookRecordsForUser(user.getUserId());
        Duration duration = Duration.ZERO;

        LocalDateTime thisMonday = LocalDateTime.now().with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime previousMonday = thisMonday.minusWeeks(1);
        Duration previousWeekDuration = Duration.ZERO;
        LocalDateTime twoWeeksAgoMonday = previousMonday.minusWeeks(1);
        Duration twoWeeksAgoDuration = Duration.ZERO;

        for (BookRecord bookRecord : books) {
            LocalDateTime begin = bookRecord.startTime();
            LocalDateTime end = bookRecord.endTime();

            if ((begin.isEqual(previousMonday) || begin.isAfter(previousMonday)) && (end.isEqual(thisMonday) || end.isBefore(thisMonday))) {
                previousWeekDuration = previousWeekDuration.plus(Duration.between(begin, end));
            }

            if ((begin.isEqual(twoWeeksAgoMonday) || begin.isAfter(twoWeeksAgoMonday)) && (end.isEqual(previousMonday) || end.isBefore(previousMonday))) {
                twoWeeksAgoDuration = twoWeeksAgoDuration.plus(Duration.between(begin, end));
            }
        }

        Duration allowedDuration = WEEKLY_LIMIT;
        if (previousWeekDuration.equals(WEEKLY_LIMIT) && twoWeeksAgoDuration.equals(WEEKLY_LIMIT)) {
            allowedDuration = PREMIUM_LIMIT;
        }

        return allowedDuration;
    }

    private boolean verifyDurationForWeek(User user, LocalDateTime start, LocalDateTime end) {
        Duration allowedDuration = getAllowedDuration(user);
        LocalDateTime thisMonday = LocalDateTime.now().with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0).withNano(0);

        LocalDateTime monday = thisMonday;
        LocalDateTime nextMonday = thisMonday.plusWeeks(1);


        // double check this loop and condition
        while (start.isAfter(nextMonday)) {
            monday = nextMonday;
            nextMonday = monday.plusWeeks(1);
        }

        if (end.isAfter(nextMonday)) {
            return false;
        }

        List<BookRecord> books = bookRecordRepository.getBookRecordsForUser(user.getUserId());
        for (BookRecord bookRecord : books) {
            LocalDateTime recordBegin = bookRecord.startTime();
            LocalDateTime recordEnd = bookRecord.endTime();
            if ((monday.isEqual(recordBegin) || monday.isBefore(recordBegin)) && (nextMonday.isEqual(recordEnd) || nextMonday.isAfter(recordEnd))) {
                allowedDuration = allowedDuration.minus(Duration.between(recordBegin, recordEnd));
            }
        }

        return Duration.between(start, end).compareTo(allowedDuration) <= 0;
    }

    public BookId book(UserId userId, AuditoryId auditoryId, LocalDateTime start, LocalDateTime end) {

        User user;
        Auditory auditory;

        try {
            user = userRepository.getUserById(userId);
        } catch (ItemNotFoundException e) {
            throw new BookException("Couldn't find user with id=" + userId, e);
        }

        try {
            auditory = auditoryRepository.getAuditoryById(auditoryId);
        } catch (ItemNotFoundException e) {
            throw new BookException("Couldn't find auditory with id=" + auditoryId, e);
        }

    }

    public List<BookRecord> getBookRecordsForUser(UserId userId) {
        try {
            return bookRecordRepository.getBookRecordsForUser(userId);
        } catch (ItemNotFoundException e) {
            throw new UserNotFoundException("Couldn't find any books for user with id=" + userId, e);
        }
    }

    public List<BookRecord> getBookRecordsForAuditory(AuditoryId auditoryId) {
        try {
            return bookRecordRepository.getBookRecordsForAuditory(auditoryId);
        } catch (ItemNotFoundException e) {
            throw new AuditoryNotFoundException("Couldn't find any books for auditory with id=" + auditoryId, e);
        }
    }

}
