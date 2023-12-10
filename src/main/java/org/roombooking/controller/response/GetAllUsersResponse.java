package org.roombooking.controller.response;

import org.roombooking.entity.User;

import java.util.List;

public record GetAllUsersResponse(List<User> users) {
}
