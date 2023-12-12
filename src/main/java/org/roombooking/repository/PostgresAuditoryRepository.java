package org.roombooking.repository;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.roombooking.entity.Auditory;
import org.roombooking.entity.id.AuditoryId;
import org.roombooking.repository.exceptions.ItemNotFoundException;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PostgresAuditoryRepository implements AuditoryRepository {

    private final Jdbi jdbi;

    public PostgresAuditoryRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public AuditoryId generateId() {
        try (Handle handle = jdbi.open()) {
            long value = (long) handle.createQuery(
                            "SELECT nextval('auditories_auditory_id_seq') AS value")
                    .mapToMap()
                    .first()
                    .get("value");
            return new AuditoryId(value);
        }
    }

    @Override
    public void addAuditory(Auditory auditory) {
        jdbi.useTransaction((Handle handle) -> handle.createUpdate(
                        "INSERT INTO auditories (auditory_id, number, available_time) VALUES (:auditoryId, :number, :availableTime)")
                .bind("auditoryId", auditory.getAuditoryId().value())
                .bind("number", auditory.getNumber())
                .bind("availableTime", auditory.getAvailableTime().stream().map((Auditory.Pair pair) -> new LocalTime[]{pair.begin(), pair.end()}).toArray())
                .execute());
    }

    @Override
    public List<Auditory> getAllAuditory() {
        return jdbi.inTransaction((Handle handle) -> handle.createQuery(
                        "SELECT auditory_id, number, available_time FROM auditories")
                .mapToMap()
                .list()
                .stream()
                .map((Map<String, Object> result) -> new Auditory(
                        new AuditoryId((long) result.get("auditory_id")),
                        (String) result.get("number"),
                        Arrays.stream((LocalTime[][]) result.get("available_time"))
                                .map(array -> new Auditory.Pair(array[0], array[1]))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList())
        );
    }

    @Override
    public Auditory getAuditoryById(AuditoryId auditoryId) {
        try {
            return jdbi.inTransaction((Handle handle) -> {
                Map<String, Object> result = handle.createQuery(
                                "SELECT auditory_id, number, available_time FROM auditories WHERE auditory_id = :userId")
                        .bind("auditoryId", auditoryId.value())
                        .mapToMap()
                        .first();
                return new Auditory(
                        (new AuditoryId((long) result.get("auditory_id"))),
                        ((String) result.get("number")),
                        (Arrays.stream((LocalTime[][]) result.get("available_time")).map(array -> new Auditory.Pair(array[0], array[1])).collect(Collectors.toList()))
                );
            });
        } catch (NullPointerException e) {
            throw new ItemNotFoundException("Couldn't find auditory with id=" + auditoryId.value());
        }
    }

    @Override
    public void update(Auditory auditory) {
        jdbi.useTransaction((Handle handle) -> {
            long updatedRaws = handle.createUpdate(
                            "SELECT * FROM auditories WHERE auditory_id = :auditoryId FOR UPDATE")
                    .bind("auditoryId", auditory.getAuditoryId().value())
                    .execute();

            if (updatedRaws == 0) {
                throw new ItemNotFoundException("Couldn't find auditory with id=" + auditory);
            }

            handle.createUpdate(
                            "UPDATE auditories SET number = :number, availableTime = :availableTime WHERE auditory_id = auditoryId")
                    .bind("auditoryId", auditory.getAuditoryId().value())
                    .bind("number", auditory.getNumber())
                    .bind("availableTime", auditory.getAvailableTime().stream().map((Auditory.Pair pair) -> new LocalTime[]{pair.begin(), pair.end()}).toArray())
                    .execute();
        });
    }
}
