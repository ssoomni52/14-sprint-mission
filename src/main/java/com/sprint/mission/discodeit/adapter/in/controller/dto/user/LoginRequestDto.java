package com.sprint.mission.discodeit.adapter.in.controller.dto.user;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(@NotBlank String username,
                              @NotBlank String password) {

}
