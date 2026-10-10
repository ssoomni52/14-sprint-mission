package com.sprint.mission.discodeit.application.binarycontent;

import com.sprint.mission.discodeit.application.binarycontent.out.BinaryContentRepository;
import com.sprint.mission.discodeit.domain.binaryContent.BinaryContent;
import com.sprint.mission.discodeit.domain.binaryContent.BinaryContentException;
import com.sprint.mission.discodeit.domain.binaryContent.BinaryContentExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    public BinaryContent findById(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new BinaryContentException(
                        BinaryContentExceptionType.BINARY_CONTENT_NOT_FOUND,
                        Map.of("id", id)
                ));
    }

    public List<BinaryContent> findAllById(List<UUID> ids) {
        return binaryContentRepository.findAllById(ids);
    }
}
