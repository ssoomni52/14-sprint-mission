package com.sprint.mission.discodeit.adapter.in.controller.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(@NotBlank String newUsername,
                                @NotBlank @Email String newEmail,
                                @NotBlank String newPassword) {

}
