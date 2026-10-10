package com.sprint.mission.discodeit.application.user.in;

import com.sprint.mission.discodeit.application.user.UserService;
import com.sprint.mission.discodeit.common.log.MutationLog;
import com.sprint.mission.discodeit.domain.user.User;
import com.sprint.mission.discodeit.adapter.in.controller.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthApplication {
    private final UserService userService;

    @MutationLog
    @Transactional(readOnly = true)
    public UserDto login(String name, String password) {
        User retrieved = userService.findByNameAndPassword(name, password);
        retrieved.updateLastActiveAt(Instant.now());
        return UserDto.from(retrieved);
    }
}
