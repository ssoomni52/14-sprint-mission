package com.sprint.mission.discodeit.adapter.in.controller.dto.channel;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ChannelUpdateNameDto(@NotEmpty String newName,
                                   @NotNull String newDescription) {
}
