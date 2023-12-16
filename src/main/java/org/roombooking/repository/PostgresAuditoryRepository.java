package org.roombooking.repository;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.postgresql.jdbc.PgArray;
import org.roombooking.entity.Auditory;
import org.roombooking.entity.id.AuditoryId;
import org.roombooking.repository.exceptions.ItemNotFoundException;

import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PostgresAuditoryRepository implements AuditoryRepository {

    private final Jdbi jdbi;

    public PostgresAuditoryRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    private LocalTime[] availableTimeArray(List<Auditory.Pair> availableTime) {
        LocalTime[] sqlArray = new LocalTime[availableTime.size() * 2];
        for (int i = 0; i < sqlArray.length; i += 2) {
            sqlArray[i] = availableTime.get(i / 2).begin();
            sqlArray[i + 1] = availableTime.get(i / 2).end();
        }
        return sqlArray;
    }

    private List<Auditory.Pair> availableTimeList(Time[] sqlArray) {
        List<Auditory.Pair> availableTime = new ArrayList<>();
        for (int i = 0; i < sqlArray.length; i+= 2) {
            availableTime.add(new Auditory.Pair(sqlArray[i].toLocalTime(), sqlArray[i + 1].toLocalTime()));
        }
        return availableTime;
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
        jdbi.registerArrayType(LocalTime.class, "TIME");
        LocalTime[] testLocalTimes = new LocalTime[] {LocalTime.of(12, 0), LocalTime.of(14, 0)};
            jdbi.useTransaction((Handle handle) -> handle.createUpdate(
                            "INSERT INTO auditories (auditory_id, number, available_time) VALUES (:auditoryId, :number, :availableTime)")
                    .bind("auditoryId", auditory.getAuditoryId().value())
                    .bind("number", auditory.getNumber())
                    .bind("availableTime",availableTimeArray(auditory.getAvailableTime()))
                    .execute());
    }

    @Override
    public List<Auditory> getAllAuditory() {
        try {
            return jdbi.inTransaction((Handle handle) -> handle.createQuery(
                            "SELECT auditory_id, number, available_time FROM auditories")
                    .mapToMap()
                    .list()
                    .stream()
                    .map((Map<String, Object> result) -> {
                        try {
                            return new Auditory(
                                    new AuditoryId((long) result.get("auditory_id")),
                                    (String) result.get("number"),
                                    (availableTimeList((Time[]) ((PgArray) result.get("available_time")).getArray())));
                        } catch (SQLException e) {
                            return null;
                        }
                    })
                    .collect(Collectors.toList())
            );
        } catch (NullPointerException e) {
            throw new ItemNotFoundException("Couldn't retrieve any auditories");
        }
    }

    @Override
    public Auditory getAuditoryById(AuditoryId auditoryId) {
        try {
            return jdbi.inTransaction((Handle handle) -> {
                Map<String, Object> result = handle.createQuery(
                                "SELECT auditory_id, number, available_time FROM auditories WHERE auditory_id = :auditoryId")
                        .bind("auditoryId", auditoryId.value())
                        .mapToMap()
                        .first();
                return new Auditory(
                        (new AuditoryId((long) result.get("auditory_id"))),
                        ((String) result.get("number")),
                        (availableTimeList((Time[]) ((PgArray) result.get("available_time")).getArray()))
                );
            });
        } catch (NullPointerException | SQLException e) {
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
                            "UPDATE auditories SET number = :number, available_time = :availableTime WHERE auditory_id = :auditoryId")
                    .bind("auditoryId", auditory.getAuditoryId().value())
                    .bind("number", auditory.getNumber())
                    .bind("availableTime", availableTimeArray(auditory.getAvailableTime()))
                    .execute();
        });
    }
}
