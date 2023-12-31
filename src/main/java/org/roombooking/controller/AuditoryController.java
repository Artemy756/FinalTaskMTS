package org.roombooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.roombooking.controller.request.*;
import org.roombooking.controller.response.*;
import org.roombooking.entity.id.AuditoryId;
import org.roombooking.repository.exceptions.ItemNotFoundException;
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
        getAuditoryById();
        getAllAuditories();
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

    private void updateName() {
        service.patch(
                "api/auditory/:id/updatename",
                (Request request, Response response) -> {
                    response.type("application/json");
                    String body = request.body();
                    AuditoryUpdateNameRequest auditoryUpdateRequest = objectMapper.readValue(body, AuditoryUpdateNameRequest.class);
                    AuditoryId auditoryId = new AuditoryId(Long.parseLong(request.params("id")));
                    try {
                        auditoryService.updateAuditoryName(auditoryId, auditoryUpdateRequest.number());
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
                "api/auditory/:id/updatetime",
                (Request request, Response response) -> {
                    response.type("application/json");
                    String body = request.body();
                    AuditoryUpdateTimeRequest auditoryUpdateRequest = objectMapper.readValue(body, AuditoryUpdateTimeRequest.class);
                    AuditoryId auditoryId = new AuditoryId(Long.parseLong(request.params("id")));
                    try {
                        auditoryService.updateAuditoryTime(auditoryId, auditoryUpdateRequest.availableTime());
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


    private void getAuditoryById() {
        service.get(
                "api/auditory/:id",
                (Request request, Response response) -> {
                    response.type("application/json");
                    AuditoryId auditoryId = new AuditoryId(Long.parseLong(request.params("id")));
                    try {
                        LOG.debug("got auditory with id={}", auditoryId);
                        response.status(201);
                        return objectMapper.writeValueAsString(new GetAuditoryResponse(auditoryService.getAuditoryById(auditoryId)));
                    } catch (AuditoryNotFoundException e) {
                        LOG.warn("Cannot find auditory by id", e);
                        response.status(400);
                        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
                    }
                }
        );
    }

    private void getAllAuditories() {
        service.get(
                "/api/auditory",
                (Request request, Response response) -> {
                    response.type("application/json");
                    try {
                        LOG.debug("find all");
                        response.status(201);
                        return objectMapper.writeValueAsString(new GetAllAuditoriesResponse(auditoryService.getAllAuditory()));
                    } catch (ItemNotFoundException e) {
                        LOG.warn("Cannot find auditory", e);
                        response.status(400);
                        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));

                    }
                }
        );
    }
}
