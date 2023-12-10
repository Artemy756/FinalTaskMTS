package org.roombooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.roombooking.controller.request.BookRequest;
import org.roombooking.controller.request.CheckIntersectionRequest;
import org.roombooking.controller.response.BookResponse;
import org.roombooking.controller.response.CheckIntersectionResponse;
import org.roombooking.controller.response.ErrorResponse;
import org.roombooking.service.BookService;
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

  }

  private void book() {
    service.patch(
            "/api/book",
            (Request request, Response response) -> {
              response.type("application/json");
              String body = request.body();
              BookRequest bookRequest = objectMapper.readValue(body, BookRequest.class);
              try {
                LOG.debug("book auditory ");
                response.status(201);
                return objectMapper.writeValueAsString(new BookResponse());
              } catch (RuntimeException e) {
                LOG.warn("Cannot book this time", e);
                response.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
              }
            }
    );
  }
  private void checkIntersection(){
    service.get(
            "/api/book",
            (Request request, Response response) -> {
              response.type("application/json");
              String body = request.body();
              CheckIntersectionRequest checkIntersectionRequest =objectMapper.readValue(body,CheckIntersectionRequest.class);
              try {
                LOG.debug("book auditory ");
                response.status(201);
                return objectMapper.writeValueAsString(new CheckIntersectionResponse());
              } catch (RuntimeException e) {
                LOG.warn("Cannot book this time", e);
                response.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
              }
            }

    );
  }

}
