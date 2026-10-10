package com.sprint.mission.discodeit.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handle(CustomException exception) {
        log.makeLoggingEventBuilder(exception.getLogLevel())
                .setCause(exception)
                .log("[{}]: {}", exception.getTitle(), exception.getDescription());
        return ResponseEntity
                .status(exception.getHttpStatusCode())
                .body(ErrorResponse.of(
                        exception.getTimestamp(),
                        exception.getExceptionTypeName(),
                        exception.getDescription(),
                        exception.getDetails(),
                        exception.getExceptionClassName(),
                        exception.getHttpStatusCode()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException exception) {
        Map<String, Object> details = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getFieldErrors()) {
            details.put(
                    fieldError.getField(),
                    fieldError.getRejectedValue()
            );
        }

        int status = HttpStatus.BAD_REQUEST.value();
        return ResponseEntity
                .status(status)
                .body(
                        ErrorResponse.of(
                                Instant.now(),
                                "REQUEST_ARGUMENTS_VALIDATION_FAILED",
                                exception.getMessage(),
                                details,
                                exception.getClass().getName(),
                                status
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Throwable throwable) {
        log.makeLoggingEventBuilder(Level.ERROR)
                .setCause(throwable)
                .log("의도하지 않은 오류 발생");
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(
                        Instant.now(),
                        throwable.getClass().getName(),
                        throwable.getMessage(),
                        null,
                        null,
                        status
                ));
    }
}
