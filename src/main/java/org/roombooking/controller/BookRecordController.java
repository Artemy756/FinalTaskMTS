package org.roombooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.roombooking.controller.request.BookRequest;
import org.roombooking.controller.request.CancelBookRequest;
import org.roombooking.controller.request.GetBookRecordsForAuditoryRequest;
import org.roombooking.controller.request.GetBookRecordsForUserRequest;
import org.roombooking.controller.response.*;
import org.roombooking.entity.id.BookId;
import org.roombooking.service.BookService;
import org.roombooking.service.exceptions.AuditoryNotFoundException;
import org.roombooking.service.exceptions.BookException;
import org.roombooking.service.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

public class BookRecordController implements Controller {
  private static final Logger LOG = LoggerFactory.getLogger(AuditoryController.class);
  private final Service service;
  private final BookService bookService;
  private final ObjectMapper objectMapper;

  public BookRecordController(Service service, BookService bookService, ObjectMapper objectMapper) {
    this.service = service;
    this.bookService = bookService;
    this.objectMapper = objectMapper;
  }

  @Override
  public void initializeEndpoints() {
    book();
    getBookRecordsForAuditory();
    getBookRecordsForUser();
    cancelbook();
  }

  private void book() {
    service.patch(
            "/api/book",
            (Request request, Response response) -> {
              response.type("application/json");
              String body = request.body();
              BookRequest bookRequest = objectMapper.readValue(body, BookRequest.class);
              try {
                BookId bookId = bookService.book(bookRequest.userId(), bookRequest.auditoryId(), bookRequest.start(), bookRequest.end());
                LOG.debug("book auditory ");
                response.status(201);
                return objectMapper.writeValueAsString(new BookResponse(bookId));
              } catch (BookException e) {
                LOG.warn("Cannot book this time", e);
                response.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
              }
            }
    );
  }

  private void cancelbook() {
    service.delete(
            "/api/book",
            (Request request, Response response) -> {
              response.type("application/json");
              String body = request.body();
              CancelBookRequest cancelBookRequest = objectMapper.readValue(body, CancelBookRequest.class);
              try {
                bookService.cancelBook(cancelBookRequest.bookId());
                LOG.debug("book auditory ");
                response.status(204);
                return objectMapper.writeValueAsString("");
              } catch (RuntimeException e) {
                LOG.warn("Cannot book this time", e);
                response.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
              }
            }

    );
  }

  private void getBookRecordsForUser() {
    service.get(
            "/api/book",
            (Request request, Response response) -> {
              response.type("application/json");
              String body = request.body();
              GetBookRecordsForUserRequest getBookRecordsForUserRequest = objectMapper.readValue(body, GetBookRecordsForUserRequest.class);
              try {
                LOG.debug("find all");
                response.status(201);
                return objectMapper.writeValueAsString(new GetBookRecordsForUserResponse(
                        bookService.getBookRecordsForUser(getBookRecordsForUserRequest.userId())));
              } catch (UserNotFoundException e) {
                LOG.warn("Cannot book this time", e);
                response.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
              }
            }
    );
  }

  private void getBookRecordsForAuditory() {
    service.get(
            "/api/book",
            (Request request, Response response) -> {
              response.type("application/json");
              String body = request.body();
              GetBookRecordsForAuditoryRequest getBookRecordsForAuditoryRequest = objectMapper.readValue(body, GetBookRecordsForAuditoryRequest.class);
              try {
                LOG.debug("find all");
                response.status(201);
                return objectMapper.writeValueAsString(new GetBookRecordsForAuditoryResponse(
                        bookService.getBookRecordsForAuditory(getBookRecordsForAuditoryRequest.auditoryId())
                ));
              } catch (AuditoryNotFoundException e) {
                LOG.warn("Cannot book this time", e);
                response.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
              }
            }
    );
  }


}
