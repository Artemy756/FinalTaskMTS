package org.roombooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.roombooking.controller.request.*;
import org.roombooking.controller.response.*;
import org.roombooking.entity.Auditory;
import org.roombooking.entity.id.AuditoryId;
import org.roombooking.service.AuditoryService;
import org.roombooking.service.exceptions.AuditoryCreateException;
import org.roombooking.service.exceptions.AuditoryNotFoundException;
import org.roombooking.service.exceptions.AuditoryUpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

public class AuditoryController implements Controller {
  private static final Logger LOG = LoggerFactory.getLogger(AuditoryController.class);
  private final Service service;
  private final AuditoryService auditoryService;
  private final ObjectMapper objectMapper;

  public AuditoryController(Service service, AuditoryService auditoryService, ObjectMapper objectMapper) {
    this.service = service;
    this.auditoryService = auditoryService;
    this.objectMapper = objectMapper;
  }


  @Override
  public void initializeEndpoints() {
    createAuditory();
    updateName();
    updateTime();
    getAllAuditory();
    getAuditoryById();
  }

  private void createAuditory() {
    service.post(
            "/api/auditory",
            (Request request, Response response) -> {
              response.type("application/json");
              String body = request.body();
              AuditoryCreateRequest auditoryCreateRequest = objectMapper.readValue(body, AuditoryCreateRequest.class);
              try {
                AuditoryId auditoryId = auditoryService.addAuditory(auditoryCreateRequest.number(), auditoryCreateRequest.availableTime());
                LOG.debug("create new auditory");
                response.status(201);
                return objectMapper.writeValueAsString(new AuditoryCreateResponse(auditoryId));
              } catch (AuditoryCreateException e) {
                LOG.warn("Cannot create auditory", e);
                response.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
              }
            }
    );
  }

  private void checkIfAvailable() {
    service.get(
            "/api/auditory",
            (Request request, Response response) -> {
              response.type("application/json");
              String body = request.body();
              CheckIfAvailableRequest checkIfAvailableRequest = objectMapper.readValue(body, CheckIfAvailableRequest.class);
              try {
                LOG.debug("check available ");
                response.status(201);
                return objectMapper.writeValueAsString(new CheckIfAvailableResponse());
              } catch (RuntimeException e) {
                LOG.warn("Cannot check", e);
                response.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
              }
            }
    );
  }

  private void updateName() {
    service.patch(
            "/api/auditory",
            (Request request, Response response) -> {
              response.type("application/json");
              String body = request.body();
              AuditoryUpdateNameRequest auditoryUpdateRequest = objectMapper.readValue(body, AuditoryUpdateNameRequest.class);
              try {
                auditoryService.updateAuditoryName(auditoryUpdateRequest.id(), auditoryUpdateRequest.number());
                LOG.debug("update auditory ");
                response.status(201);
                return objectMapper.writeValueAsString("");
              } catch (AuditoryUpdateException e) {
                LOG.warn("Cannot update auditory", e);
                response.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
              }
            }
    );

  }

  private void updateTime() {
    service.patch(
            "/api/auditory",
            (Request request, Response response) -> {
              response.type("application/json");
              String body = request.body();
              AuditoryUpdateTimeRequest auditoryUpdateRequest = objectMapper.readValue(body, AuditoryUpdateTimeRequest.class);
              try {
                auditoryService.updateAuditoryTime(auditoryUpdateRequest.id(), auditoryUpdateRequest.availableTime());
                LOG.debug("update auditory ");
                response.status(201);
                return objectMapper.writeValueAsString("");
              } catch (AuditoryUpdateException e) {
                LOG.warn("Cannot update auditory", e);
                response.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
              }
            }
    );

  }

  private void getAllAuditory() {
    service.get(
            "/api/auditory",
            (Request request, Response response) -> {
              response.type("application/json");
              String body = request.body();
              LOG.debug("find all");
              response.status(201);
              return objectMapper.writeValueAsString(new GetAllAuditoryResponse(auditoryService.getAllAuditory()));
            }
    );
  }

  private void getAuditoryById() {
    service.get(
            "/api/auditory",
            (Request request, Response response) -> {
              response.type("application/json");
              String body = request.body();
              GetAuditoryByIdRequest getAuditoryByIdRequest = objectMapper.readValue(body, GetAuditoryByIdRequest.class);
              try {
                Auditory auditory = auditoryService.getAuditoryById(getAuditoryByIdRequest.auditoryId());
                LOG.debug("check available ");
                response.status(201);
                return objectMapper.writeValueAsString(new GetAuditoryResponse(auditory));
              } catch (AuditoryNotFoundException e) {
                throw new AuditoryNotFoundException("Cannot find auditory by id=" + getAuditoryByIdRequest.auditoryId(), e);
              }
            }
    );
  }
}
