package com.sprint.mission.discodeit.domain.user;

import com.sprint.mission.discodeit.common.exception.ExceptionType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum UserExceptionType implements ExceptionType {
    LOGIN_FAILED(HttpStatus.BAD_REQUEST, Level.INFO, "로그인 실패", "잘못된 username 혹은 password 입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, Level.INFO, "User 미존재", "Database에서 해당 User를 찾을 수 없음."),
    USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, Level.INFO, "UserStatus 미존재", "Database에서 해당 UserStatus 찾을 수 없음"),
    USER_STATUS_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, Level.INFO, "UserStatus 중복", "해당 UserStatus 이미 존재"),
    USER_UNIQUE_FIELD_CONFLICT(HttpStatus.BAD_REQUEST, Level.INFO, "User unique 필드 중복", "(Name, Email) 필드 중복 불가"),
    USER_MISSING_UPSERT_ARGUMENT(HttpStatus.BAD_REQUEST, Level.INFO, "User 갱신 인자 누락", "User create/update시 name, email, password가 반드시 필요함")
    ;

    HttpStatus httpStatus;
    Level logLevel;
    String title;
    String description;
}
