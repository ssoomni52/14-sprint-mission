package com.sprint.mission.discodeit.adapter.out.persistence;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.domain.channel.Channel;
import com.sprint.mission.discodeit.application.channel.out.ChannelRepositoryCustom;
import com.sprint.mission.discodeit.domain.channel.ChannelType;
import com.sprint.mission.discodeit.domain.channel.QChannel;
import com.sprint.mission.discodeit.domain.readstatus.QReadStatus;
import com.sprint.mission.discodeit.domain.user.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ChannelRepositoryCustomImpl implements ChannelRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QChannel channel = QChannel.channel;

    @Override
    public List<Channel> findAccessibleByUserId(UUID userId) {
        QUser participant = new QUser("participant");
        return queryFactory
                .selectFrom(channel).distinct()
                .leftJoin(channel.participants, participant).fetchJoin()
                .leftJoin(participant.status).fetchJoin()
                .leftJoin(participant.profile).fetchJoin()
                .where(
                        channelTypeEqualsPublic()
                        .or(readStatusExists(userId))
                )
                .fetch();
    }

    private BooleanExpression channelTypeEqualsPublic() {
        return channel.type.eq(ChannelType.PUBLIC);
    }

    private BooleanExpression readStatusExists(UUID userId) {
        QReadStatus readStatus = QReadStatus.readStatus;
        return queryFactory
                .selectFrom(readStatus)
                .where(
                        readStatus.user.id.eq(userId),
                        readStatus.channel.eq(channel)
                )
                .exists();
    }

}
