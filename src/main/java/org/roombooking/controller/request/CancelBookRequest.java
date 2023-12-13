package org.roombooking.controller.request;

import org.roombooking.entity.id.BookId;

public record CancelBookRequest(BookId bookId) {
}
