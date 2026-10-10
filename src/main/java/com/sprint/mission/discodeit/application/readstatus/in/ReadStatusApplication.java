package com.sprint.mission.discodeit.application.readstatus.in;

import com.sprint.mission.discodeit.common.log.MutationLog;
import com.sprint.mission.discodeit.domain.channel.Channel;
import com.sprint.mission.discodeit.domain.readstatus.ReadStatus;
import com.sprint.mission.discodeit.domain.user.User;
import com.sprint.mission.discodeit.adapter.in.controller.dto.readStatus.ReadStatusResponse;
import com.sprint.mission.discodeit.application.channel.ChannelService;
import com.sprint.mission.discodeit.application.readstatus.ReadStatusService;
import com.sprint.mission.discodeit.application.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadStatusApplication {
    private final ReadStatusService readStatusService;
    private final UserService userService;
    private final ChannelService channelService;

    @MutationLog
    @Transactional
    public ReadStatusResponse create(UUID userId, UUID channelId, Instant lastReadAt) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);
        ReadStatus created = readStatusService.create(
                ReadStatus.of(user, channel, lastReadAt)
        );
        return ReadStatusResponse.of(created);
    }

    @Transactional
    public List<ReadStatusResponse> getAllReadStatusByUserId(UUID userId) {
        userService.validateExistsById(userId);
        return readStatusService.findAllByUserId(userId).stream()
                .map(ReadStatusResponse::of)
                .toList();
    }

    @MutationLog
    public ReadStatusResponse updateReadStatus(UUID publicReadStatusId, Instant newLastReadAt) {
        ReadStatus updated = readStatusService.update(publicReadStatusId, newLastReadAt);
        return ReadStatusResponse.of(updated);
    }
}
