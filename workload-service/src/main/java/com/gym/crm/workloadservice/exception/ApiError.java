package com.gym.crm.workloadservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
enum ApiError {
    INVALID_REQUEST_ERROR(2400, "Invalid or malformed request data", BAD_REQUEST),
    VALIDATION_ERROR(2760, "Validation error: ", BAD_REQUEST),
    NOT_FOUND_ERROR(2835, "Requested data was not found: ", NOT_FOUND),
    SERVER_ERROR(3200, "Internal processing error", INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ApiError(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
