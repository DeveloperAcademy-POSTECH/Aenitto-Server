package com.firefighter.aenitto.messages.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.UUID;

@Repository
@Qualifier(value = "messageRepositoryImpl")
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository{
    private final EntityManager em;

    @Override
    public int findUnreadMessageCount(UUID memberId, Long roomId) {
        return em.createQuery(
                        "SELECT COUNT(*)" +
                                " FROM Message m" +
                                " JOIN m.receiver rc" +
                                " JOIN m.room r" +
                                " WHERE rc.id = :memberId" +
                                " AND r.id = :roomId" +
                                " AND m.read = :read", Long.class)
                .setParameter("memberId", memberId)
                .setParameter("roomId", roomId)
                .setParameter("read", false)
                .getSingleResult()
                .intValue();
    }
}
