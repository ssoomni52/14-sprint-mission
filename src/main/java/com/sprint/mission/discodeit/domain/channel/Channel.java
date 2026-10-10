package com.sprint.mission.discodeit.domain.channel;

import com.sprint.mission.discodeit.domain.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "channels")
@ToString(onlyExplicitlyIncluded = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseUpdatableEntity {
    @ToString.Include
    @Column(length = 100)
    private String name;
    @ToString.Include
    @Column(length = 500)
    private String description;

    @ToString.Include
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ChannelType type;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "read_statuses",
            joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants = new ArrayList<>();

    private Channel(ChannelType type, String name, String description) {
        super();
        this.type = type;
        this.name = name;
        this.description = description;
    }

    private Channel(ChannelType channelType, String name) {
        this(channelType, name, null);
    }

    public static Channel createPublicChannel(String name, String description) {
        return new Channel(ChannelType.PUBLIC, name, description);
    }

    public static Channel createPrivateChannel(List<UUID> usersId) {
        String name = usersId.stream()
                .map(UUID::toString)
                .collect(Collectors.joining(", "));
        return new Channel(ChannelType.PRIVATE, name);
    }

    public Channel updateNameAndDescription(String name, String description) {
        if (type.equals(ChannelType.PRIVATE)) {
            throw new ChannelException(
                    ChannelExceptionType.PRIVATE_CHANNEL_CANNOT_BE_MODIFIED,
                    Map.of(
                            "id", this.getId(),
                            "channelType", this.getType()
                    )
            );
        }
        this.name = name;
        this.description = description;
        return this;
    }

    public boolean isPrivate() {
        return type.equals(ChannelType.PRIVATE);
    }

    public boolean isPublic() {
        return type.equals(ChannelType.PUBLIC);
    }
}
