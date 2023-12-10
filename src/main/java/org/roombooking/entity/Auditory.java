package org.roombooking.entity;

import org.roombooking.entity.id.AuditoryId;

import java.time.LocalTime;
import java.util.List;

public class Auditory {
    private final AuditoryId auditoryId;
    private final String number;
    private final List<Pair> availableTime;

    public AuditoryId getAuditoryId() {
        return auditoryId;
    }

    public String getNumber() {
        return number;
    }

    public List<Pair> getAvailableTime() {
        return availableTime;
    }

    public record Pair(LocalTime begin, LocalTime end) {
    }

    public Auditory(AuditoryId auditoryId, String number, List<Pair> availableTime) {
        this.auditoryId = auditoryId;
        this.number = number;
        this.availableTime = availableTime;
    }
}
