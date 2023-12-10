package org.roombooking.controller.request;

import org.roombooking.entity.id.AuditoryId;

public record CheckIfAvailableRequest(AuditoryId auditoryId) {
}
