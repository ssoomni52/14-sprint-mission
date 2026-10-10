package com.sprint.mission.discodeit.domain.message;

import com.sprint.mission.discodeit.common.exception.CustomException;

import java.util.Map;

public final class MessageException extends CustomException {
    public MessageException(MessageExceptionType type, Map<String, Object> details) {
        super(type, details);
    }
}
