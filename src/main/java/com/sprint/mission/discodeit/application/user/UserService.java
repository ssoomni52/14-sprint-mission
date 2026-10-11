package com.sprint.mission.discodeit.application.user;

import com.sprint.mission.discodeit.domain.binaryContent.BinaryContent;
import com.sprint.mission.discodeit.domain.user.User;
import com.sprint.mission.discodeit.domain.user.UserException;
import com.sprint.mission.discodeit.domain.user.UserExceptionType;
import com.sprint.mission.discodeit.application.user.out.UserRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(User creating) {
        validateNameAndEmailAvailable(creating.getUsername(), creating.getEmail());
        return userRepository.save(creating);
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException(
                                UserExceptionType.USER_NOT_FOUND,
                                Map.of("id", id)
                        )
                );
    }

    public List<User> findByIds(List<UUID> userIds) {
        return userRepository.findByIdIn(userIds);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteById(UUID id) {
        User deleting = findById(id);
        userRepository.delete(deleting);
    }

    public User findByNameAndPassword(String name, String password) {
        validateExistsByName(name);
        return userRepository.findByUsernameAndPassword(name, password)
                .orElseThrow(() -> new UserException(
                                UserExceptionType.LOGIN_FAILED,
                                Map.of(
                                        "name", name,
                                        "password", password
                                )
                        )
                );
    }

    public User update(UUID id, String username, String email, String password, @Nullable BinaryContent profile) {
        validateUsernameAndEmailAvailableWhereUserNot(username, email, id);
        User updating = findById(id);
        return updating.update(username, email, password, profile);
    }


    public User updateNewLastActiveAt(UUID id, Instant newLastActiveAt) {
        User updating = findById(id);
        return updating.updateLastActiveAt(newLastActiveAt);
    }

    public void validateExistsById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserException(UserExceptionType.USER_NOT_FOUND, Map.of("id", id));
        }
    }

    private void validateExistsByName(String name) {
        if (!userRepository.existsByUsername(name)) {
            throw new UserException(UserExceptionType.USER_NOT_FOUND, Map.of("name", name));
        }
    }

    private void validateNameAndEmailAvailable(String name, String email) {
        if (userRepository.existsByUsernameOrEmail(name, email)) {
            throw new UserException(
                    UserExceptionType.USER_UNIQUE_FIELD_CONFLICT,
                    Map.of(
                            "name,", name,
                            "email", email
                    )
            );
        }
    }

    private void validateUsernameAndEmailAvailableWhereUserNot(String username, String email, UUID id) {
        if (userRepository.existsByUsernameAndIdNot(username, id)) {
            throw new UserException(
                    UserExceptionType.USER_UNIQUE_FIELD_CONFLICT,
                    Map.of(
                            "username", username,
                            "id", id
                    )
            );
        }
        if (userRepository.existsByEmailAndIdNot(email, id)) {
            throw new UserException(
                    UserExceptionType.USER_UNIQUE_FIELD_CONFLICT,
                    Map.of(
                            "email", email,
                            "id", id
                    )
            );
        }
    }

    public void flush() {
        userRepository.flush();
    }
}
