package org.roombooking.repository;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.roombooking.entity.User;
import org.roombooking.entity.id.UserId;
import org.roombooking.repository.exceptions.ItemNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PostgresUserRepository implements UserRepository {

    private final Jdbi jdbi;

    public PostgresUserRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public UserId generateId() {
        try (Handle handle = jdbi.open()) {
            long value = (long) handle.createQuery("SELECT nextval('users_user_id_seq') AS value")
                    .mapToMap()
                    .first()
                    .get("value");
            return new UserId(value);
        }
    }

    @Override
    public void addUser(User user) {
        jdbi.useTransaction((Handle handle) -> handle.createUpdate(
                "INSERT INTO users (user_id, name, phone_number, email) VALUES (:userId, :name, :phoneNumber, :email)")
                .bind("userId", user.getUserId().value())
                .bind("name", user.getName())
                .bind("phoneNumber", user.getPhoneNumber())
                .bind("email", user.getEmail())
                .execute());
    }

    @Override
    public List<User> getAllUsers() {
        try {
            return jdbi.inTransaction((Handle handle) -> handle.createQuery(
                            "SELECT user_id, name, phone_number, email FROM auditories")
                    .mapToMap()
                    .list()
                    .stream()
                    .map((Map<String, Object> result) -> new User(
                            new UserId((long) result.get("user_id")),
                            (String) result.get("name"),
                            (String) result.get("phone_number"),
                            (String) result.get("email")))
                    .collect(Collectors.toList())
            );
        } catch(NullPointerException e) {
            throw new ItemNotFoundException("Couldn't retrieve any users");
        }
    }

    @Override
    public User getUserById(UserId userId) {
        try {
            return jdbi.inTransaction((Handle handle) -> {
                Map<String, Object> result = handle.createQuery(
                                "SELECT user_id, name, phone_number, email FROM users WHERE user_id = :userId")
                        .bind("userId", userId.value())
                        .mapToMap()
                        .first();
                return new User(
                        (new UserId((long) result.get("user_id"))),
                        ((String) result.get("name")),
                        ((String) result.get("phone_number")),
                        ((String) result.get("email"))
                );
            });
        } catch (NullPointerException e) {
            throw new ItemNotFoundException("Couldn't find user with id=" + userId.value());
        }
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        try {
            return jdbi.inTransaction((Handle handle) -> {
                Map<String, Object> result = handle.createQuery(
                                "SELECT user_id, name, phone_number, email FROM users WHERE phone_number = :phoneNumber")
                        .bind("phoneNumber", phoneNumber)
                        .mapToMap()
                        .first();
                return new User(
                        (new UserId((long) result.get("user_id"))),
                        ((String) result.get("name")),
                        ((String) result.get("phone_number")),
                        ((String) result.get("email"))
                );
            });
        } catch (NullPointerException e) {
            throw new ItemNotFoundException("Couldn't find user with phoneNumber=" + phoneNumber);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            return jdbi.inTransaction((Handle handle) -> {
                Map<String, Object> result = handle.createQuery(
                                "SELECT user_id, name, phone_number, email FROM users WHERE email = :email")
                        .bind("email", email)
                        .mapToMap()
                        .first();
                return new User(
                        (new UserId((long) result.get("user_id"))),
                        ((String) result.get("name")),
                        ((String) result.get("phone_number")),
                        ((String) result.get("email"))
                );
            });
        } catch (NullPointerException e) {
            throw new ItemNotFoundException("Couldn't find user with email=" + email);
        }
    }
}
