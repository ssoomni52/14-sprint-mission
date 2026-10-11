package com.sprint.mission.discodeit.application.user.in;

import com.sprint.mission.discodeit.adapter.in.controller.dto.user.UserDto;
import com.sprint.mission.discodeit.adapter.in.controller.multipart.CreateBinaryContentCommand;
import com.sprint.mission.discodeit.application.binarycontent.out.BinaryContentStorage;
import com.sprint.mission.discodeit.application.readstatus.ReadStatusService;
import com.sprint.mission.discodeit.application.user.UserService;
import com.sprint.mission.discodeit.domain.binaryContent.BinaryContent;
import com.sprint.mission.discodeit.domain.user.User;
import com.sprint.mission.discodeit.domain.user.UserException;
import com.sprint.mission.discodeit.domain.user.UserExceptionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserApplicationTest {
    @Mock
    UserService userService;
    @Mock
    ReadStatusService readStatusService;
    @Mock
    BinaryContentStorage binaryContentStorage;

    @InjectMocks
    UserApplication userApplication;

    @Test
    @DisplayName("User 생성 성공 시, 생성 정보를 반환하고 프로필을 저장한다.")
    void whenCreateUser_thenReturnCreatedUserAndSaveProfile() {
        String username = "test";
        String email = "test@gmail.com";
        String password = "1234";
        CreateBinaryContentCommand command = createBinaryContentCommand();

        when(userService.create(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        UserDto response = userApplication.createAccount(username, email, password, command);

        // then
        assertThat(response.username()).isEqualTo(username);
        assertThat(response.email()).isEqualTo(email);
        assertThat(response.profile().getFileName()).isEqualTo(command.fileName());
        assertThat(response.profile().getContentType()).isEqualTo(command.contentType());
        assertThat(response.profile().getSize()).isEqualTo(command.content().length);

        verify(userService).create(argThat(user ->
                user.getUsername().equals(username)
                        && user.getEmail().equals(email)
                        && user.getPassword().equals(password)
                        && user.getProfile() != null
        ));
        verify(binaryContentStorage).put(
                response.profile().getId(),
                command.content()
        );
    }

    @Test
    @DisplayName("User 생성 실패 시, 예외를 던지고 프로필을 저장하지 않는다.")
    void whenCreateFailed_thenThrowException() {
        String username = "test";
        String email = "test@gmail.com";
        String password = "1234";
        CreateBinaryContentCommand command = createBinaryContentCommand();

        UserException expectedException = userUniqueFieldConflict(username, email);
        when(userService.create(any(User.class)))
                .thenThrow(expectedException);

        // when && then
        assertThatThrownBy(() ->
                userApplication.createAccount(username, email, password, command)
        ).isSameAs(expectedException);

        verify(userService).create(any(User.class));
        verifyNoInteractions(binaryContentStorage);
    }

    @Test
    @DisplayName("User 갱신 수정 성공 시, 수정 정보를 반환하고 프로필을 저장한다.")
    void whenUpdateUser_thenReturnUpdatedUser() {
        UUID userId = UUID.randomUUID();
        User existing = User.create(
                "existing",
                "existing@gmail.com",
                "existing",
                null
        );
        String newUsername = "new";
        String newEmail = "new@gmail.com";
        String newPassword = "new";
        CreateBinaryContentCommand command = createBinaryContentCommand();

        when(userService.update(eq(userId), eq(newUsername), eq(newEmail), eq(newPassword), any(BinaryContent.class)))
                .thenAnswer(invocation -> existing.update(
                        newUsername,
                        newEmail,
                        newPassword,
                        invocation.getArgument(4)
                ));

        // when
        UserDto response = userApplication.updateUser(userId, newUsername, newEmail, newPassword, command);

        // then
        assertThat(response.username()).isEqualTo(newUsername);
        assertThat(response.email()).isEqualTo(newEmail);
        assertThat(response.profile().getFileName()).isEqualTo(command.fileName());
        assertThat(response.profile().getContentType()).isEqualTo(command.contentType());
        assertThat(response.profile().getSize()).isEqualTo(command.content().length);

        verify(userService).update(
                eq(userId),
                eq(newUsername),
                eq(newEmail),
                eq(newPassword),
                any(BinaryContent.class)
        );
        verify(userService).flush();
        verify(binaryContentStorage).put(
                response.profile().getId(),
                command.content()
        );
    }

    @Test
    @DisplayName("프로필 요창이 null일 때, User 수정 성공 시, 수정 정보를 반환한다.")
    void givenNullProfile_whenUpdateUser_thenReturnOnlyUpdatedUser() {
        UUID userId = UUID.randomUUID();
        User existing = User.create(
                "existing",
                "existing@gmail.com",
                "existing",
                BinaryContent.of("existingProfile", "png", new byte[]{1})
        );
        String newUsername = "new";
        String newEmail = "new@gmail.com";
        String newPassword = "new";
        CreateBinaryContentCommand command = null;

        when(userService.update(eq(userId), eq(newUsername), eq(newEmail), eq(newPassword), isNull()))
                .thenAnswer(invocation -> existing.update(
                        newUsername,
                        newEmail,
                        newPassword,
                        null
                ));


        // when
        UserDto updated = userApplication.updateUser(userId, newUsername, newEmail, newPassword, command);

        // then
        assertThat(updated.username()).isEqualTo(newUsername);
        assertThat(updated.email()).isEqualTo(newEmail);
        assertThat(updated.profile().getFileName()).isEqualTo(existing.getProfile().getFileName());
        assertThat(updated.profile().getContentType()).isEqualTo(existing.getProfile().getContentType());
        assertThat(updated.profile().getSize()).isEqualTo(existing.getProfile().getSize());

        verify(userService).update(
                eq(userId),
                eq(newUsername),
                eq(newEmail),
                eq(newPassword),
                isNull()
        );
        verify(userService).flush();
        verifyNoInteractions(binaryContentStorage);
    }

    @Test
    @DisplayName("User 갱신 실패 시, 예외를 던지고 프로필을 수정하지 않는다.")
    void whenUpdateUserFailed_thenThrowExceptionAndDoNotUpdateProfile() {
        UUID userId = UUID.randomUUID();
        String newUsername = "duplicated";
        String newEmail = "duplicated@gmail.com";
        String newPassword = "new";
        CreateBinaryContentCommand command = createBinaryContentCommand();

        // when
        UserException expectedException = userUniqueFieldConflict(newUsername, newEmail);
        when(userService.update(
                eq(userId),
                eq(newUsername),
                eq(newEmail),
                eq(newPassword),
                any(BinaryContent.class))
        ).thenThrow(expectedException);

        // then
        assertThatThrownBy(() ->
                userApplication.updateUser(userId, newUsername, newEmail, newPassword, command)
        ).isSameAs(expectedException);

        verify(userService).update(eq(userId), eq(newUsername), eq(newEmail), eq(newPassword), any(BinaryContent.class));
        verifyNoInteractions(binaryContentStorage);
    }


    private CreateBinaryContentCommand createBinaryContentCommand() {
        return CreateBinaryContentCommand.of(
                "profile",
                "png",
                new byte[]{1}
        );
    }

    private UserException userUniqueFieldConflict(String username, String email) {
        return new UserException(
                UserExceptionType.USER_UNIQUE_FIELD_CONFLICT,
                Map.of(
                        "username", username,
                        "email", email
                ));
    }
}
