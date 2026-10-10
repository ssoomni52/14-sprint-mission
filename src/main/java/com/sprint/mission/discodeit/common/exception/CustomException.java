package com.sprint.mission.discodeit.common.exception;

import lombok.Getter;
import org.slf4j.event.Level;

import java.time.Instant;
import java.util.Map;

@Getter
public abstract class CustomException extends RuntimeException{
    private final Instant timestamp;
    private final ExceptionType type;
    private final Map<String, Object> details;

    protected CustomException(ExceptionType type, Map<String, Object> details) {
        super(type.getTitle() + type.getDescription());
        this.timestamp = Instant.now();
        this.type = type;
        this.details = details;
    }

    public String getExceptionClassName() {
        return getClass().getName();
    }

    public String getExceptionTypeName() {
        return type.toString();
    }

    public int getHttpStatusCode() {
        return type.getHttpStatus().value();
    }

    public Level getLogLevel() {
        return type.getLogLevel();
    }

    public String getTitle() {
        return type.getTitle();
    }

    public String getDescription() {
        return type.getDescription();
    }
}
