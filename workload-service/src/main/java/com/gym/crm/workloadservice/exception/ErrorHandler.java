package com.gym.crm.workloadservice.exception;

import com.gym.crm.openapi.model.ErrorResponse;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gym.crm.workloadservice.exception.ApiError.INVALID_REQUEST_ERROR;
import static com.gym.crm.workloadservice.exception.ApiError.NOT_FOUND_ERROR;
import static com.gym.crm.workloadservice.exception.ApiError.SERVER_ERROR;
import static com.gym.crm.workloadservice.exception.ApiError.VALIDATION_ERROR;

@ControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundExceptions(Exception ex) {
        log.error("Entity not found Exception: {}", ex.getMessage(), ex);

        return buildErrorResponse(NOT_FOUND_ERROR, ex.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(NullPointerException ex) {
        log.error("NullPointer Exception: {}", ex.getMessage());

        return buildErrorResponse(INVALID_REQUEST_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(Exception ex) {
        log.error("Validation Exception: {}", ex.getMessage(), ex);

        String cleanedMessage = extractConstraintMessage(ex.getMessage());

        return buildErrorResponse(VALIDATION_ERROR, cleanedMessage);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleUnhandledExceptions(Exception ex) {
        log.error("Unhandled Exception: {}", ex.getMessage(), ex);

        return buildErrorResponse(SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(ApiError apiError) {
        return buildErrorResponse(apiError, null);

    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(ApiError apiError, String message) {
        message = StringUtils.isBlank(message) ? "" : message;

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(apiError.getCode());
        errorResponse.setErrorMessage(apiError.getMessage() + message);

        return new ResponseEntity<>(errorResponse, apiError.getHttpStatus());
    }

    private String extractConstraintMessage(String rawMessage) {
        Pattern pattern = Pattern.compile(":\\s([^:]+)$");
        Matcher matcher = pattern.matcher(rawMessage);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return rawMessage;
    }
}
