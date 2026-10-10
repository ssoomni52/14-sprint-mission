package com.sprint.mission.discodeit.application.message.in;

import com.sprint.mission.discodeit.application.message.MessageCreateService;
import com.sprint.mission.discodeit.adapter.in.controller.multipart.CreateBinaryContentCommand;
import com.sprint.mission.discodeit.application.channel.ChannelService;
import com.sprint.mission.discodeit.common.log.MutationLog;
import com.sprint.mission.discodeit.domain.message.Message;
import com.sprint.mission.discodeit.application.message.MessageService;
import com.sprint.mission.discodeit.adapter.in.controller.dto.common.PageResponse;
import com.sprint.mission.discodeit.adapter.in.controller.dto.message.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MessageApplication {
    private final MessageService messageService;
    private final ChannelService channelService;
    private final MessageCreateService messageCreateService;

    @MutationLog
    @Transactional
    public MessageResponseDto createMessage(String content, UUID channelId, UUID userId,
                                            List<CreateBinaryContentCommand> createFileCommands) {
        Message created = messageCreateService.create(content, channelId, userId, createFileCommands);
        return MessageResponseDto.of(created);
    }

    @Transactional
    public PageResponse<MessageResponseDto, Instant> getAllByChannelId(
            UUID channelId,
            Instant cursor,
            Pageable pageable
    ) {
        channelService.validateExists(channelId);
        Slice<MessageResponseDto> retrieved = messageService.findAllByChannelId(
                        channelId,
                        Optional.ofNullable(cursor).orElse(Instant.now(Clock.systemDefaultZone())),
                        pageable
                )
                .map(MessageResponseDto::of);

        return PageResponse.fromSlice(
                retrieved,
                MessageResponseDto::createdAt
        );
    }

    @MutationLog
    @Transactional
    public MessageResponseDto updateMessage(UUID id, String content) {
        Message updated = messageService.updateContent(id, content);
        return MessageResponseDto.of(updated);
    }

    @MutationLog
    @Transactional
    public MessageResponseDto deleteMessage(UUID id) {
        Message deleted = messageService.deleteById(id);
        return MessageResponseDto.of(deleted);
    }
}
