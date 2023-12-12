package org.roombooking.controller.request;

import org.roombooking.entity.id.AuditoryId;
import org.roombooking.entity.id.UserId;

import java.time.LocalDateTime;

public record BookRequest(UserId userId, AuditoryId auditoryId, LocalDateTime start, LocalDateTime end) {
}
