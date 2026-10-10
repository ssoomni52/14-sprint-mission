package com.sprint.mission.discodeit.domain.binaryContent;

import com.sprint.mission.discodeit.common.exception.CustomException;

import java.util.Map;

public final class BinaryContentException extends CustomException {
    public BinaryContentException(BinaryContentExceptionType type, Map<String, Object> details) {
        super(type, details);
    }
}
