package org.roombooking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.roombooking.controller.AuditoryController;
import org.roombooking.controller.BookRecordController;
import org.roombooking.controller.UserController;
import org.roombooking.repository.*;
import org.roombooking.service.AuditoryService;
import org.roombooking.service.BookService;
import org.roombooking.service.UserService;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import spark.Service;

import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.util.List;

@Testcontainers
class MainTest {

    @Container
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");

    private static Jdbi jdbi;
    private static Service service;
    public static ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll() {
        String postgresJdbcUrl = POSTGRES.getJdbcUrl();
        Flyway flyway = Flyway.configure()
                .outOfOrder(true)
                .locations("classpath:db/migrations")
                .dataSource(postgresJdbcUrl, POSTGRES.getUsername(), POSTGRES.getPassword())
                .load();
        flyway.migrate();
        jdbi = Jdbi.create(postgresJdbcUrl, POSTGRES.getUsername(), POSTGRES.getPassword());
        jdbi.registerArrayType(LocalTime.class, "TIME");
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    void beforeEach() {
        jdbi.useTransaction((Handle handle) -> {
            handle.createUpdate("DELETE FROM auditories").execute();
            handle.createUpdate("DELETE FROM users").execute();
            handle.createUpdate("DELETE FROM books").execute();
        });

        service = Service.ignite();
    }

    @AfterEach
    void afterEach() {
        service.stop();
        service.awaitStop();
    }

    @Test
    void endToEndTest() {
        AuditoryRepository auditoryRepository = new PostgresAuditoryRepository(jdbi);
        UserRepository userRepository = new PostgresUserRepository(jdbi);
        BookRecordRepository bookRecordRepository = new PostgresBookRecordRepository(jdbi);
        AuditoryService auditoryService = new AuditoryService(auditoryRepository);
        UserService userService = new UserService(userRepository);
        BookService bookService = new BookService(bookRecordRepository, userRepository, auditoryRepository);
        Application application = new Application(List.of(
                new AuditoryController(service, auditoryService, objectMapper),
                new UserController(service, userService, objectMapper),
                new BookRecordController(service, bookService, objectMapper)
        ));
        application.start();
        service.awaitInitialization();
        HttpResponse<String> response;


    }

}