package org.roombooking.repository;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.roombooking.entity.User;
import org.roombooking.entity.id.UserId;

import java.util.List;

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

    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public User getUserById(UserId userId) {
        return null;
    }

    @Override
    public User getUserById(int id) {
        return null;
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        return null;
    }
}
