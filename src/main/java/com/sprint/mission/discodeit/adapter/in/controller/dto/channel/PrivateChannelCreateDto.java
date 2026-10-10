package com.sprint.mission.discodeit.adapter.in.controller.dto.channel;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateDto(@NotEmpty List<UUID> participantIds) {

}
