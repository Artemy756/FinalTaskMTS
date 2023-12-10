package org.roombooking.entity;


import org.roombooking.entity.id.AuditoryId;
import org.roombooking.entity.id.BookId;
import org.roombooking.entity.id.UserId;

import java.time.LocalDateTime;

public record BookRecord(BookId bookId, UserId userId, AuditoryId auditoryId, LocalDateTime time) {

}
