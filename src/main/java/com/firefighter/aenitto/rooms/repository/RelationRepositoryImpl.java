package com.firefighter.aenitto.rooms.repository;

import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.domain.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class RelationRepositoryImpl implements RelationRepository {
    private final EntityManager em;

    @Override
    public Optional<Relation> findByRoomIdAndMemberId(Long roomId, UUID memberId) {
        return em.createQuery(
                "SELECT r" +
                " FROM Relation r" +
                " WHERE r.room.id = :roomId" +
                " AND r.manitto.id = :memberId", Relation.class)
                .setParameter("roomId", roomId)
                .setParameter("memberId", memberId)
                .getResultList().stream().findFirst();
    }
}
