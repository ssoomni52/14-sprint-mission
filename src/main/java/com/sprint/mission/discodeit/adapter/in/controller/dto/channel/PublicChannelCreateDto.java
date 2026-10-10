package com.sprint.mission.discodeit.adapter.in.controller.dto.channel;

import jakarta.validation.constraints.NotBlank;

public record PublicChannelCreateDto(@NotBlank String name,
                                     @NotBlank String description) {

}
