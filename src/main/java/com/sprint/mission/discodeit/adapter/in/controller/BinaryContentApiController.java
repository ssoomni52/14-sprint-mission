package com.sprint.mission.discodeit.adapter.in.controller;

import com.sprint.mission.discodeit.adapter.in.controller.dto.binaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.application.binarycontent.in.BinaryContentApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/binaryContents")
public class BinaryContentApiController {
    private final BinaryContentApplication binaryContentApplication;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<BinaryContentResponseDto> getBinaryContents(@RequestParam List<UUID> binaryContentIds) {
        return binaryContentApplication.getAllBinaryContentInfos(binaryContentIds);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{binaryContentId}")
    public BinaryContentResponseDto getBinaryContent(@PathVariable UUID binaryContentId) {
        return binaryContentApplication.getBinaryContentInfo(binaryContentId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{binaryContentId}/download")
    public ResponseEntity<?> downloadBinaryContent(@PathVariable UUID binaryContentId) {
        return binaryContentApplication.getBinaryContent(binaryContentId);
    }
}
