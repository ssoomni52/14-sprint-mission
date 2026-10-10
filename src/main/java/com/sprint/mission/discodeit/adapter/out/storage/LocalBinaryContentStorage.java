package com.sprint.mission.discodeit.adapter.out.storage;

import com.sprint.mission.discodeit.application.binarycontent.out.BinaryContentStorage;
import com.sprint.mission.discodeit.adapter.in.controller.dto.binaryContent.BinaryContentResponseDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(
        name = "discodeit.storage.type",
        havingValue = "local"
)
public class LocalBinaryContentStorage implements BinaryContentStorage {
    private final Path root;

    public LocalBinaryContentStorage(
            @Value(value = "${discodeit.storage.local.root-path}") Path root
    ) {
        this.root = root;
    }

    @PostConstruct
    public void init() {
        if (Files.exists(root)) {
            return;
        }

        try {
            Files.createDirectory(root);
        } catch (IOException e) {
            throw new StorageException(StorageExceptionType.LOCAL_STORAGE_DIRECTORY_CREATION_FAILED, Collections.emptyMap());
        }
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            throw new StorageException(StorageExceptionType.LOCAL_STORAGE_FILE_ALREADY_EXISTS, Collections.emptyMap());
        }

        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new StorageException(StorageExceptionType.LOCAL_STORAGE_FILE_CREATION_FAILED, Collections.emptyMap());
        }
        return id;
    }

    @Override
    public InputStream get(UUID id) {
        Path path = resolvePath(id);
        if (Files.notExists(path)) {
            throw new StorageException(StorageExceptionType.LOCAL_STORAGE_FILE_NOT_FOUND, Collections.emptyMap());
        }

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new StorageException(StorageExceptionType.LOCAL_STORAGE_FILE_READ_FAILED, Collections.emptyMap());
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentResponseDto binaryContentResponse) {
        InputStreamResource resource = new InputStreamResource(get(binaryContentResponse.id()));
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(binaryContentResponse.contentType()))
                .body(resource);
    }

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }

}
