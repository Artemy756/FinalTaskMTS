package org.roombooking.controller.request;

import org.roombooking.entity.Auditory;

import java.util.List;

public record AuditoryUpdateTimeRequest(List<Auditory.Pair> availableTime) {
}
