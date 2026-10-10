package com.sprint.mission.discodeit.adapter.in.controller.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(@NotBlank String username,
                                @NotBlank @Email String email,
                                @NotBlank String password) {

}
