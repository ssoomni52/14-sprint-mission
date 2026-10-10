package com.sprint.mission.discodeit.application.message;

import com.sprint.mission.discodeit.adapter.in.controller.multipart.CreateBinaryContentCommand;
import com.sprint.mission.discodeit.domain.binaryContent.BinaryContent;
import com.sprint.mission.discodeit.domain.channel.Channel;
import com.sprint.mission.discodeit.domain.channel.ChannelException;
import com.sprint.mission.discodeit.domain.channel.ChannelExceptionType;
import com.sprint.mission.discodeit.application.channel.ChannelService;
import com.sprint.mission.discodeit.domain.message.Message;
import com.sprint.mission.discodeit.domain.user.User;
import com.sprint.mission.discodeit.application.binarycontent.out.BinaryContentStorage;
import com.sprint.mission.discodeit.application.readstatus.ReadStatusService;
import com.sprint.mission.discodeit.application.user.UserService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageCreateService {
    private final UserService userService;
    private final ChannelService channelService;
    private final ReadStatusService readStatusService;
    private final MessageService messageService;
    private final BinaryContentStorage binaryContentStorage;

    public Message create(String content, UUID channelId, UUID userId,
                          List<CreateBinaryContentCommand> createFileCommands) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        if (channel.isPrivate() && !readStatusService.existsByUserAndChannel(userId, channelId)) {
            throw new ChannelException(
                    ChannelExceptionType.NO_ACCESS_TO_CHANNEL,
                    Map.of(
                            "channel", channelId,
                            "userId", userId
                    )
            );
        }

        List<BinaryContent> createdAttachments = createFiles(createFileCommands);
        Message created = messageService.create(
                Message.of(
                        content,
                        user,
                        channel,
                        createdAttachments
                )
        );

        saveCreatedAttachments(createdAttachments, createFileCommands);
        return created;
    }

    private void saveCreatedAttachments(List<BinaryContent> createdAttachments, List<CreateBinaryContentCommand> createFileCommands) {
        if (Objects.nonNull(createFileCommands) && !createFileCommands.isEmpty()) {
            for (int i = 0; i < createFileCommands.size(); i++) {
                UUID id = createdAttachments.get(i).getId();
                byte[] content = createFileCommands.get(i).content();
                binaryContentStorage.put(id, content);
            }
        }
    }

    private @Nullable List<BinaryContent> createFiles(List<CreateBinaryContentCommand> createFileCommands) {
        if (Objects.isNull(createFileCommands) || createFileCommands.isEmpty()) {
            return null;
        }

        return createFileCommands.stream()
                .map(command ->
                        BinaryContent.of(
                                command.fileName(),
                                command.contentType(),
                                command.content()
                        )
                )
                .toList();
    }
}
