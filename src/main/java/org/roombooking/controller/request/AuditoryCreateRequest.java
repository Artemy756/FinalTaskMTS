package org.roombooking.controller.request;

import java.time.LocalDateTime;
import java.util.List;

public record AuditoryCreateRequest(String number, List<LocalDateTime> availableTime) {
}
