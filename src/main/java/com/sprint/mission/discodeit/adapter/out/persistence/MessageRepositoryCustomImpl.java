package com.sprint.mission.discodeit.adapter.out.persistence;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.application.message.out.MessageRepositoryCustom;
import com.sprint.mission.discodeit.domain.message.QMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryCustomImpl implements MessageRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    QMessage message = QMessage.message;

    @Override
    public List<Tuple> findLatestMessageByChannelId(List<UUID> channelIds) {
        return queryFactory
                .select(message.channel.id, message.createdAt.max())
                .from(message)
                .where(message.channel.id.in(channelIds))
                .groupBy(message.channel.id)
                .fetch();
    }
}
