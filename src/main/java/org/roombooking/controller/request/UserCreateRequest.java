package org.roombooking.controller.request;

public record UserCreateRequest(String name, String phoneNumber, String email) {
}
