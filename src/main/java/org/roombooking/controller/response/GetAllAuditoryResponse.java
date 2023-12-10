package org.roombooking.controller.response;

import org.roombooking.entity.Auditory;

import java.util.List;

public record GetAllAuditoryResponse(List<Auditory> auditories) {
}
