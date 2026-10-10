package com.sprint.mission.discodeit.adapter.in.controller;

import com.sprint.mission.discodeit.adapter.in.controller.multipart.CreateBinaryContentCommand;
import com.sprint.mission.discodeit.adapter.in.controller.multipart.MultiPartFileUtil;
import com.sprint.mission.discodeit.adapter.in.controller.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.adapter.in.controller.dto.user.UserDto;
import com.sprint.mission.discodeit.adapter.in.controller.dto.user.UserStatusDto;
import com.sprint.mission.discodeit.adapter.in.controller.dto.user.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.adapter.in.controller.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.application.user.in.UserApplication;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequestMapping(value = "/api/users")
@RestController
@RequiredArgsConstructor
public class UserApiController {
    private final UserApplication userApplication;
    private final MultiPartFileUtil multiPartFileUtil;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDto create(@Valid @RequestPart(required = true) UserCreateRequest userCreateRequest,
                          @RequestPart(required = false) MultipartFile profile) {
        CreateBinaryContentCommand createProfileCommand = createCommandIfNotNullOrElseGetNull(profile);
        return userApplication.createAccount(
                userCreateRequest.username(),
                userCreateRequest.email(),
                userCreateRequest.password(),
                createProfileCommand
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/{userId}")
    public UserDto update(@PathVariable UUID userId,
                          @Valid @RequestPart UserUpdateRequest userUpdateRequest,
                          @RequestPart(required = false) MultipartFile profile) {
        CreateBinaryContentCommand createProfileCommand = createCommandIfNotNullOrElseGetNull(profile);
        return userApplication.updateUser(
                userId,
                userUpdateRequest.newUsername(),
                userUpdateRequest.newEmail(),
                userUpdateRequest.newPassword(),
                createProfileCommand
        );
    }

    private CreateBinaryContentCommand createCommandIfNotNullOrElseGetNull(MultipartFile file) {
        return Objects.nonNull(file) ?
                multiPartFileUtil.convert(file) : null;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{userId}")
    public void delete(@PathVariable UUID userId) {
        userApplication.deleteAccount(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<UserDto> findAll() {
        return userApplication.getAllUsers();
    }

    @GetMapping(value = "/{id}")
    public UserDto read(@PathVariable UUID id) {
        return userApplication.getUser(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/{userId}/userStatus")
    public UserStatusDto updateUserStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UserStatusUpdateRequest request
    ) {
        return userApplication.updateUserStatus(userId, request.newLastActiveAt());
    }
}
