package com.sprint.mission.discodeit.adapter.out.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.domain.readstatus.QReadStatus;
import com.sprint.mission.discodeit.domain.readstatus.ReadStatus;
import com.sprint.mission.discodeit.application.readstatus.out.ReadStatusRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReadStatusRepositoryCustomImpl implements ReadStatusRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final QReadStatus readStatus = QReadStatus.readStatus;

    @Override
    public List<UUID> findAllUserIdByChannelId(UUID channelId) {
        return queryFactory
                .select(readStatus.user.id)
                .from(readStatus)
                .where(readStatus.channel.id.eq(channelId))
                .fetch();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return queryFactory
                .selectFrom(readStatus)
                .where(readStatus.user.id.eq(userId))
                .fetch();
    }
}
