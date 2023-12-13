package org.roombooking.controller.response;

import org.roombooking.entity.BookRecord;
import org.roombooking.entity.User;

import java.util.List;

public record GetBookRecordsForUserResponse(List<BookRecord> bookRecords) {
}
