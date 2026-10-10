package com.sprint.mission.discodeit.application.message;

import com.querydsl.core.Tuple;
import com.sprint.mission.discodeit.application.message.out.MessageRepository;
import com.sprint.mission.discodeit.domain.message.Message;
import com.sprint.mission.discodeit.domain.message.MessageException;
import com.sprint.mission.discodeit.domain.message.MessageExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public Message create(Message message) {
        return messageRepository.save(message);
    }

    public Message findById(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new MessageException(
                                MessageExceptionType.MESSAGE_NOT_FOUND,
                                Map.of("id", id)
                        )
                );
    }

    public Slice<Message> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
        return messageRepository.findAllByChannelId(
                channelId,
                cursor,
                pageable
        );
    }

    public Message updateContent(UUID id, String content) {
        return findById(id).updateContent(content);
    }

    public Message deleteById(UUID id) {
        Message deleting = findById(id);
        messageRepository.delete(deleting);
        return deleting;
    }

    public void deleteAllByChannelId(UUID channelId) {
        messageRepository.deleteAllByChannel_Id(channelId);
    }

    public Map<UUID, Instant> findLastMessageAtByChannelId(List<UUID> channelIds) {
        List<Tuple> latestMessageByChannelId = messageRepository.findLatestMessageByChannelId(channelIds);

        return latestMessageByChannelId.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, UUID.class),
                        tuple -> Optional.ofNullable(tuple.get(1, Instant.class)).orElse(null)
                ));
    }
}
