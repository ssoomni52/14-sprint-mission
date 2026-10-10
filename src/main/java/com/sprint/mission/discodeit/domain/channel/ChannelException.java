package com.sprint.mission.discodeit.domain.channel;

import com.sprint.mission.discodeit.common.exception.CustomException;

import java.util.Map;

public final class ChannelException extends CustomException {
    public ChannelException(ChannelExceptionType type, Map<String, Object> details) {
        super(type, details);
    }
}
