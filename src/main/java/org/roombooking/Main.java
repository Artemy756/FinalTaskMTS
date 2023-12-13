package org.roombooking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.roombooking.controller.AuditoryController;
import org.roombooking.controller.BookRecordController;
import org.roombooking.controller.BookRecordFreeMakerController;
import org.roombooking.controller.UserController;
import org.roombooking.repository.PostgresAuditoryRepository;
import org.roombooking.repository.PostgresBookRecordRepository;
import org.roombooking.repository.PostgresUserRepository;
import org.roombooking.service.AuditoryService;
import org.roombooking.service.BookService;
import org.roombooking.service.UserService;
import spark.Service;

import java.util.List;

public class Main {
  public static void main(String[] args) {
    Service service = Service.ignite();
    Config config = ConfigFactory.load();
    ObjectMapper objectMapper = new ObjectMapper();
    Jdbi jdbi = Jdbi.create(config.getString("app.database.url"), config.getString("app.database.user"),
            config.getString("app.database.password"));
    Flyway flyway =
            Flyway.configure()
                    .outOfOrder(true)
                    .locations("classpath:db/migrations")
                    .dataSource(config.getString("app.database.url"), config.getString("app.database.user"),
                            config.getString("app.database.password"))
                    .load();
    flyway.migrate();
    final var auditoryService = new AuditoryService(new PostgresAuditoryRepository(jdbi));
    final var bookService = new BookService( new PostgresBookRecordRepository(jdbi),new PostgresUserRepository(jdbi),new PostgresAuditoryRepository(jdbi));
    final var userService = new UserService(new PostgresUserRepository(jdbi));
    Aplication aplication = new Aplication(
            List.of(
                    new UserController(
                            service,
                            userService,
                            objectMapper
                    ), new AuditoryController(
                            service,
                            auditoryService,
                            objectMapper
                    ), new BookRecordController(
                            service,
                            bookService,
                            objectMapper

                    ), new BookRecordFreeMakerController(
                            service,
                            bookService,
                            TemplateFactory.freeMarkerEngine()
                    )
            )
    );
    aplication.start();
  }
}