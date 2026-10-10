package com.sprint.mission.discodeit.domain.user;

import com.sprint.mission.discodeit.common.exception.CustomException;

import java.util.Map;

public final class UserException extends CustomException {

    public UserException(UserExceptionType type, Map<String, Object> details) {
        super(type, details);
    }
}
