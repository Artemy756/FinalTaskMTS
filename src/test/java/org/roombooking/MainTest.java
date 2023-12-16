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
import org.roombooking.entity.Auditory;
import org.roombooking.entity.id.AuditoryId;
import org.roombooking.entity.id.BookId;
import org.roombooking.entity.id.UserId;
import org.roombooking.repository.*;
import org.roombooking.service.AuditoryService;
import org.roombooking.service.BookService;
import org.roombooking.service.UserService;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import spark.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;


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
    void endToEndTest() throws IOException, InterruptedException {
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
        UserId userId = userService.addUser("a", "1223", "test.email");
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .POST(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        {"name":"User","phoneNumber":"122456","email":"email@yandex.ru"}"""
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/user".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(201, response.statusCode());
        userService.getAllUser();
        HttpResponse<String> response1 = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .GET(
                                )
                                .uri(URI.create("http://localhost:%d/api/user".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(201, response1.statusCode());
        userService.getUserById(userId);
        HttpResponse<String> response2 = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .GET(
                                )
                                .uri(URI.create("http://localhost:%d/api/user/:id".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(201, response2.statusCode());
        userService.getUserByEmail("test.email");
        HttpResponse<String> response3 = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .GET(
                                )
                                .uri(URI.create("http://localhost:%d/api/user/by-email".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(201, response3.statusCode());
        userService.getUserByPhoneNumber("1223");
        HttpResponse<String> response5 = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .GET(
                                )
                                .uri(URI.create("http://localhost:%d/api/user/by-phone-number".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(201, response5.statusCode());
        List<Auditory.Pair> time = new ArrayList<>();
        AuditoryId auditoryId = auditoryService.addAuditory("2", time);
        HttpResponse<String> response4 = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .POST(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        {"number":"three","availableTime":[{"begin":[6,0],"end":[19,0]},{"begin":[21,0],"end":[21,30]}]}""")
                                )
                                .uri(URI.create("http://localhost:%d/api/auditory".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(201, response4.statusCode());
        auditoryService.updateAuditoryName(auditoryId, "4");
        HttpResponse<String> response6 = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .PUT(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        {
                                                        "number": "two"
                                                        }""")
                                )
                                .uri(URI.create("http://localhost:%d/api/auditory/:id/updatename".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(201, response6.statusCode());
        auditoryService.getAllAuditory();
        HttpResponse<String> response7 = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .GET(
                                )
                                .uri(URI.create("http://localhost:%d/api/auditory".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(201, response7.statusCode());
        auditoryService.getAuditoryById(auditoryId);
        HttpResponse<String> response8 = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .GET(
                                )
                                .uri(URI.create("http://localhost:%d/api/auditory/:id".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(201, response8.statusCode());
        auditoryService.updateAuditoryTime(auditoryId, time);
        HttpResponse<String> response9 = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .PUT(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        {"availableTime":[{"begin":[7,0],"end":[18,0]},{"begin":[20,0],"end":[22,0]}]}""")
                                )
                                .uri(URI.create("http://localhost:%d/api/auditory/:id/updatetime".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(201, response9.statusCode());
        LocalDateTime start = LocalDateTime.of(2023, 12, 21, 12, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 21, 13, 0, 0);
        BookId bookId = bookService.book(userId, auditoryId, start, end);
        HttpResponse<String> response10 = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .PUT(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                                                                                {
                                                        {"userId":{"value":3},"auditoryId":{"value":1},"start":[2023,12,17,18,0],"end":[2023,12,17,19,0]}                                                        }""")
                                )
                                .uri(URI.create("http://localhost:%d/api/book".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(201, response10.statusCode());
        bookService.cancelBook(bookId);
        HttpResponse<String> response11 = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                .method("DELETE", HttpRequest.BodyPublishers.ofString("""
                        {"bookId":{"value":3}}"""))
                .uri(URI.create("http://localhost:%d/api/book/cancel".formatted(service.port()))).build(), HttpResponse.BodyHandlers.ofString(UTF_8)
        );
        assertEquals(204, response11.statusCode());
        bookService.getBookRecordsForUser(userId);
        HttpResponse<String> response12 = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                .method("GET", HttpRequest.BodyPublishers.ofString("""
                        {"userId":{"value":3}}"""))
                .uri(URI.create("http://localhost:%d/api/book/for-user".formatted(service.port()))).build(), HttpResponse.BodyHandlers.ofString(UTF_8)
        );
        assertEquals(201, response12.statusCode());
        bookService.getBookRecordsForUser(userId);
        HttpResponse<String> response13 = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                .method("GET", HttpRequest.BodyPublishers.ofString("""
                        {"auditoryId":{"value":3}}"""))
                .uri(URI.create("http://localhost:%d/api/book/for-auditory".formatted(service.port()))).build(), HttpResponse.BodyHandlers.ofString(UTF_8)
        );
        assertEquals(201, response13.statusCode());

    }

}

