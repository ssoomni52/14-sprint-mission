package com.sprint.mission.discodeit.application.channel;

import com.sprint.mission.discodeit.application.channel.out.ChannelRepository;
import com.sprint.mission.discodeit.domain.channel.Channel;
import com.sprint.mission.discodeit.domain.channel.ChannelException;
import com.sprint.mission.discodeit.domain.channel.ChannelExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;

    public Channel create(Channel channel) {
        return channelRepository.save(channel);
    }

    public Channel findById(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new ChannelException(
                        ChannelExceptionType.CHANNEL_NOT_FOUND_IN_DATABASE,
                        Map.of("id", id)
                ));
    }

    public List<Channel> findAccessibleByUserId(UUID userId) {
        return channelRepository.findAccessibleByUserId(userId);
    }

    public void deleteById(UUID id) {
        Channel deleting = findById(id);
        channelRepository.delete(deleting);
    }

    public Channel updateNameAndDescription(UUID id, String name, String description) {
        Channel updating = findById(id);
        return updating.updateNameAndDescription(name, description);
    }

    public void validateExists(UUID id) {
        if (!channelRepository.existsById(id)) {
            throw new ChannelException(
                    ChannelExceptionType.CHANNEL_NOT_FOUND_IN_DATABASE,
                    Map.of("id", id)
            );
        }
    }
}
