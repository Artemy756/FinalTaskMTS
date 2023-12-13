package org.roombooking.controller.request;

import org.roombooking.entity.id.UserId;

public record GetBookRecordsForUserRequest(UserId userId) {
}
