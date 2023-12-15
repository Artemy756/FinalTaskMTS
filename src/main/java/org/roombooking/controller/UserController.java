package org.roombooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.roombooking.controller.request.*;
import org.roombooking.controller.response.*;
import org.roombooking.entity.User;
import org.roombooking.entity.id.AuditoryId;
import org.roombooking.entity.id.UserId;
import org.roombooking.service.UserService;
import org.roombooking.service.exceptions.AuditoryNotFoundException;
import org.roombooking.service.exceptions.UserCreateException;
import org.roombooking.service.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

public class UserController implements Controller {
  private static final Logger LOG = LoggerFactory.getLogger(AuditoryController.class);
  private final Service service;
  private final UserService userService;
  private final ObjectMapper objectMapper;

  public UserController(Service service, UserService userService, ObjectMapper objectMapper) {
    this.service = service;
    this.userService = userService;
    this.objectMapper = objectMapper;
  }

  @Override
  public void initializeEndpoints() {
    addUser();
    getUserById();
    getUserByEmail();
    getUserByPhoneNumber();
    getAllUsers();
  }

  private void addUser() {
    service.post(
            "/api/user",
            (Request request, Response response) -> {
              response.type("application/json");
              String body = request.body();
              UserCreateRequest userCreateRequest = objectMapper.readValue(body, UserCreateRequest.class);
              try {
                UserId userId = userService.addUser(userCreateRequest.name(), userCreateRequest.phoneNumber(), userCreateRequest.email());
                LOG.debug("create new user");
                response.status(201);
                return objectMapper.writeValueAsString(new UserCreateResponse(userId));
              } catch (UserCreateException e) {
                LOG.warn("Cannot create user", e);
                response.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
              }
            }
    );
  }

  private void getUserById() {
    service.get(
            "/api/user/:id",
            (Request request, Response response) -> {
              response.type("application/json");
              String body = request.body();
              GetUserByIdRequest getUserByIdRequest = objectMapper.readValue(body, GetUserByIdRequest.class);
              try {
                UserId userId = new UserId(Long.parseLong(request.params("auditoryId")));
                LOG.debug("get user by id");
                response.status(201);
                return objectMapper.writeValueAsString(new GetUserResponse(userService.getUserById(userId)));
              } catch (UserNotFoundException e) {
                throw new UserNotFoundException("Cannot find user by id=" + getUserByIdRequest.userId(), e);
              }
            }

    );
  }

  private void getUserByEmail() {
    service.get(
            "/api/user/:id",
            (Request request, Response response) -> {
              response.type("application/json");
              String body = request.body();
              GetUserByEmailRequest getUserByEmailRequest = objectMapper.readValue(body, GetUserByEmailRequest.class);
              try {
                LOG.debug("get user by email");
                response.status(201);
                return objectMapper.writeValueAsString(new GetUserResponse(userService.getUserByEmail(getUserByEmailRequest.email())));
              } catch (UserNotFoundException e) {
                throw new UserNotFoundException("Cannot find user by email=" + getUserByEmailRequest.email(), e);
              }
            }

    );
  }

  private void getUserByPhoneNumber() {
    service.get(
            "/api/user/:id",
            (Request request, Response response) -> {
              response.type("application/json");
              String body = request.body();
              GetUserByPhoneNumberRequst getUserByPhoneNumberRequst = objectMapper.readValue(body, GetUserByPhoneNumberRequst.class);
              try {
                LOG.debug("get user by phone number");
                response.status(201);
                return objectMapper.writeValueAsString(new GetUserResponse(userService.getUserByPhoneNumber(getUserByPhoneNumberRequst.number())));
              } catch (UserNotFoundException e) {
                throw new UserNotFoundException("Cannot find user by number=" + getUserByPhoneNumberRequst.number(), e);
              }
            }

    );
  }

  private void getAllUsers() {
    service.get(
            "/api/auditory",
            (Request request, Response response) -> {
              response.type("application/json");
              try {
                LOG.debug("find all");
                response.status(201);
                return objectMapper.writeValueAsString(new GetAllUsersResponse(userService.getAllUser()));
              } catch (UserNotFoundException e) {
                throw new UserNotFoundException("Cannot find user ", e);
              }
            }
    );
  }

}
