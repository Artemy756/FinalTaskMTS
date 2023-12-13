package org.roombooking.controller.response;

import org.roombooking.entity.BookRecord;

import java.util.List;

public record GetBookRecordsForAuditoryResponse(List<BookRecord> bookRecords) {
}
