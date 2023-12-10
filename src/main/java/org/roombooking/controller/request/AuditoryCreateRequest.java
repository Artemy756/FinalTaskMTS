package org.roombooking.controller.request;

import org.roombooking.entity.Auditory;

import java.time.LocalDateTime;
import java.util.List;

public record AuditoryCreateRequest(String number, List<Auditory.Pair> availableTime) {
}
