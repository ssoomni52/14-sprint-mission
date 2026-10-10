package com.sprint.mission.discodeit.application.channel.in;

import com.sprint.mission.discodeit.common.log.MutationLog;
import com.sprint.mission.discodeit.domain.user.User;
import com.sprint.mission.discodeit.adapter.in.controller.dto.channel.ChannelUpsertResponse;
import com.sprint.mission.discodeit.adapter.in.controller.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.domain.channel.Channel;
import com.sprint.mission.discodeit.domain.readstatus.ReadStatus;
import com.sprint.mission.discodeit.application.channel.ChannelService;
import com.sprint.mission.discodeit.application.message.MessageService;
import com.sprint.mission.discodeit.application.readstatus.ReadStatusService;
import com.sprint.mission.discodeit.application.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChannelApplication {
    private final ChannelService channelService;
    private final MessageService messageService;
    private final UserService userService;
    private final ReadStatusService readStatusService;

    @MutationLog
    @Transactional
    public ChannelUpsertResponse createPublicChannel(String name, String description) {
        Channel creating = Channel.createPublicChannel(name, description);
        Channel created = channelService.create(creating);
        return ChannelUpsertResponse.of(created);
    }

    @MutationLog
    @Transactional
    public ChannelUpsertResponse createPrivateChannel(List<UUID> userIds) {
        List<User> users = userService.findByIds(userIds);
        Channel creatingChannel = Channel.createPrivateChannel(userIds);
        Channel createdChannel = channelService.create(creatingChannel);

        // 참여 User의 정보를 받아 User 별 ReadStatus 정보 생성
        List<ReadStatus> readStatuses = ReadStatus.of(users, createdChannel, Instant.now());
        readStatusService.createAll(readStatuses);
        return ChannelUpsertResponse.of(createdChannel);
    }

    // 1. DTO를 활용해 가장 최근 메시지의 시간 정보 포함
    // 2. PRIVATE 채널인 경우 참여한 User의 id정보 반환
    // 3. 특정 User가 볼 수 있는 Channel 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다.
    // 4. PUBLIC인 전체조회, PRIVATE은 User가 참여한 채널만 조회하도록
    @Transactional
    public List<ChannelResponseDto> getAllChannelsByUserId(UUID userId) {
        userService.validateExistsById(userId);
        List<Channel> accessibleChannels = channelService.findAccessibleByUserId(userId);
        List<UUID> channelIds = accessibleChannels.stream()
                .map(Channel::getId)
                .toList();
        Map<UUID, Instant> channelLastMessageAt = messageService.findLastMessageAtByChannelId(channelIds);

        return accessibleChannels.stream()
                .map(channel -> ChannelResponseDto.of(
                        channel,
                        channel.getParticipants(),
                        Optional.ofNullable(channelLastMessageAt.get(channel.getId())).orElse(channel.getUpdatedAt())
                ))
                .toList();
    }

    @MutationLog
    @Transactional
    public ChannelUpsertResponse updateChannelName(UUID id, String name, String description) {
        Channel updated = channelService.updateNameAndDescription(id, name, description);
        return ChannelUpsertResponse.of(updated);
    }

    @MutationLog
    @Transactional
    public void deleteChannel(UUID id) {
        // 채널 내부 message 삭제
        messageService.deleteAllByChannelId(id);
        readStatusService.deleteByChannelId(id);
        channelService.deleteById(id);
    }
}
