package com.sprint.mission.discodeit.application.user.in;

import com.sprint.mission.discodeit.adapter.in.controller.dto.user.UserDto;
import com.sprint.mission.discodeit.adapter.in.controller.multipart.CreateBinaryContentCommand;
import com.sprint.mission.discodeit.application.binarycontent.out.BinaryContentStorage;
import com.sprint.mission.discodeit.application.readstatus.ReadStatusService;
import com.sprint.mission.discodeit.application.user.UserService;
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
    @DisplayName("유저 생성 성공")
    void whenCreateUser_thenReturnCreatedUser() {
        String username = "test";
        String email = "test@gmail.com";
        String password = "1234";
        CreateBinaryContentCommand command = CreateBinaryContentCommand.of(
                "profile",
                "png",
                new byte[]{1}
        );

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
    @DisplayName("유저 생성 실패 시, profile 저장하지 않음 검증")
    void whenCreateFailed_thenThrowException() {
        String username = "test";
        String email = "test@gmail.com";
        String password = "1234";
        CreateBinaryContentCommand command = CreateBinaryContentCommand.of(
                "profile",
                "png",
                new byte[]{1}
        );

        UserException expectedException = new UserException(
                UserExceptionType.USER_UNIQUE_FIELD_CONFLICT,
                Map.of(
                        "username", username,
                        "email", email
                ));
        when(userService.create(any(User.class)))
                .thenThrow(expectedException);

        // when && then
        assertThatThrownBy(() ->
                userApplication.createAccount(username, email, password, command)
        ).isSameAs(expectedException);

        verifyNoInteractions(binaryContentStorage);
    }
}