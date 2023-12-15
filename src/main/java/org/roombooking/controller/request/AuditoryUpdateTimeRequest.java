package org.roombooking.controller.request;

import org.roombooking.entity.Auditory;
import org.roombooking.entity.id.AuditoryId;

import java.time.LocalDateTime;
import java.util.List;

public record AuditoryUpdateTimeRequest(List<Auditory.Pair> availableTime) {
}
