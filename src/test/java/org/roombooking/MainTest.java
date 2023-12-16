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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
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
        HttpResponse<String> response2 = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .GET(
                                )
                                .uri(URI.create("http://localhost:%d/api/user/1".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(201, response2.statusCode());
        HttpResponse<String> response3 = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                .method("GET", HttpRequest.BodyPublishers.ofString("""
                        {"email":"email@yandex.ru"}"""))
                .uri(URI.create("http://localhost:%d/api/user/by-email".formatted(service.port()))).build(), HttpResponse.BodyHandlers.ofString(UTF_8)
        );
        assertEquals(201, response3.statusCode());
        HttpResponse<String> response5 = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                .method("GET", HttpRequest.BodyPublishers.ofString("""
                        {"phoneNumber":"122456"}"""))
                .uri(URI.create("http://localhost:%d/api/user/by-phone-number".formatted(service.port()))).build(), HttpResponse.BodyHandlers.ofString(UTF_8)
        );
        assertEquals(201, response5.statusCode());
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
                                .uri(URI.create("http://localhost:%d/api/auditory/1/updatename/two".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(201, response6.statusCode());
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
        HttpResponse<String> response8 = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .GET(
                                )
                                .uri(URI.create("http://localhost:%d/api/auditory/1".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(201, response8.statusCode());
        HttpResponse<String> response9 = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .PUT(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        {"availableTime":[{"begin":[7,0],"end":[18,0]},{"begin":[20,0],"end":[22,0]}]}""")
                                )
                                .uri(URI.create("http://localhost:%d/api/auditory/1/updatetime".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(201, response9.statusCode());
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
        HttpResponse<String> response11 = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                .method("DELETE", HttpRequest.BodyPublishers.ofString("""
                        {"bookId":{"value":3}}"""))
                .uri(URI.create("http://localhost:%d/api/book/cancel".formatted(service.port()))).build(), HttpResponse.BodyHandlers.ofString(UTF_8)
        );
        assertEquals(204, response11.statusCode());
        HttpResponse<String> response12 = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                .method("GET", HttpRequest.BodyPublishers.ofString("""
                        {"userId":{"value":3}}"""))
                .uri(URI.create("http://localhost:%d/api/book/for-user".formatted(service.port()))).build(), HttpResponse.BodyHandlers.ofString(UTF_8)
        );
        assertEquals(201, response12.statusCode());
        HttpResponse<String> response13 = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                .method("GET", HttpRequest.BodyPublishers.ofString("""
                        {"auditoryId":{"value":3}}"""))
                .uri(URI.create("http://localhost:%d/api/book/for-auditory".formatted(service.port()))).build(), HttpResponse.BodyHandlers.ofString(UTF_8)
        );
        assertEquals(201, response13.statusCode());

    }

}

