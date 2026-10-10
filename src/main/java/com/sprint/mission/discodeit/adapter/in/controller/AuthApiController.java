package com.sprint.mission.discodeit.adapter.in.controller;

import com.sprint.mission.discodeit.adapter.in.controller.dto.user.LoginRequestDto;
import com.sprint.mission.discodeit.adapter.in.controller.dto.user.UserDto;
import com.sprint.mission.discodeit.application.user.in.AuthApplication;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthApiController {
    private final AuthApplication authApplication;

    @PostMapping(value = "/login")
    public UserDto login(@Valid @RequestBody LoginRequestDto requestDto) {
        return authApplication.login(requestDto.username(), requestDto.password());
    }
}
