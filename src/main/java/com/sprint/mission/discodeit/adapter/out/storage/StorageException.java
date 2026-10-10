package com.sprint.mission.discodeit.adapter.out.storage;

import com.sprint.mission.discodeit.common.exception.CustomException;

import java.util.Map;

public final class StorageException extends CustomException {
    public StorageException(StorageExceptionType type, Map<String, Object> details) {
        super(type, details);
    }
}
