package com.firefighter.aenitto.messages.repository;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.messages.domain.Message;
import com.firefighter.aenitto.rooms.domain.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@Repository
@Qualifier(value = "messageRepositoryImpl")
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {
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

    @Override
    public Message saveMessage(Message message) {
        em.persist(message);
        return message;
    }

    @Override
    public List<Message> getSentMessages(UUID senderId, Long roomId) {
        return em.createQuery(
                        "SELECT m " +
                                "FROM Message m " +
                                "WHERE m.sender.id = :memberId " +
                                "AND m.room.id = :roomId", Message.class)
                .setParameter("memberId", senderId)
                .setParameter("roomId", roomId)
                .getResultList();
    }

    @Override
    public List<Message> findMessagesByReceiverIdAndRoomIdAndStatus(UUID receiverId, Long roomId, boolean status) {
        return em.createQuery(
                        "SELECT m " +
                                "FROM Message m " +
                                "WHERE m.receiver.id = :receiverId " +
                                "AND m.room.id = :roomId " +
                                "AND m.read = :status", Message.class)
                .setParameter("receiverId", receiverId)
                .setParameter("roomId", roomId)
                .setParameter("status", status)
                .getResultList();
    }
}
