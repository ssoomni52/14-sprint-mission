package com.sprint.mission.discodeit.domain.user;

import com.sprint.mission.discodeit.domain.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.domain.binaryContent.BinaryContent;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
@ToString(onlyExplicitlyIncluded = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {
    @ToString.Include
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    @Column(nullable = false, length = 60)
    private String password;

    @ToString.Include
    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    @JoinColumn(unique = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private BinaryContent profile;

    @OneToOne(
            mappedBy = "user",
            optional = false,
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    private UserStatus status;


    public static User create(
            String username,
            String email,
            String password,
            BinaryContent createdProfile
    ) {
        User user = new User(username, email, password, createdProfile);
        user.status = UserStatus.create(user);
        return user;
    }

    public User(String username, String email, String password, @Nullable BinaryContent profile) {
        super();
        if (Objects.isNull(username) || Objects.isNull(email) || Objects.isNull(password)) {
            throw new UserException(
                    UserExceptionType.USER_MISSING_UPSERT_ARGUMENT,
                    Map.of(
                            "username", username,
                            "email", email,
                            "password", password
                    )
            );
        }
        assignIfNotNull(profile, () -> this.profile = profile);
    }

    public User update(String username, String email, String password, @Nullable BinaryContent profile) {
        assignIfNotNull(username, () -> this.username = username);
        assignIfNotNull(email, () -> this.email = email);
        assignIfNotNull(password, () -> this.password = password);
        assignIfNotNull(profile, () -> this.profile = profile);
        return this;
    }


    private <T> void assignIfNotNull(T t, Runnable runnable) {
        if (Objects.nonNull(t)) {
            runnable.run();
        }
    }

    public boolean isOnline() {
        return status.isOnline();
    }

    public Instant getLastSeenAt() {
        return status.getLastSeenAt();
    }

    public UUID getStatusId() {
        return this.status.getId();
    }

    public User updateLastActiveAt(Instant newLastActiveAt) {
        this.status.updateLastSeenAt(newLastActiveAt);
        return this;
    }
}
