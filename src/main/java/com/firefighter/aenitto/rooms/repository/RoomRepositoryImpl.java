package com.firefighter.aenitto.rooms.repository;

import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Qualifier(value = "roomRepositoryImpl")
@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepository {
    private final EntityManager em;

    @Override
    public Room saveRoom(Room room) {
        em.persist(room);
        return room;
    }

    @Override
    public Room mergeRoom(Room room) {
        return em.merge(room);
    }

    @Override
    public Optional<Room> findRoomById(Long id) {
        return Optional.ofNullable(em.find(Room.class, id));
    }

    @Override
    public Optional<Room> findByInvitation(String invitation) {
        return em.createQuery(
                        "SELECT r" +
                                " FROM Room r" +
                                " WHERE r.invitation = :invitation" +
                                " AND r.deleted = FALSE", Room.class)
                .setParameter("invitation", invitation)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Optional<MemberRoom> findMemberRoomById(UUID memberId, Long roomId) {
        return em.createQuery(
                        "SELECT mr" +
                                " FROM MemberRoom mr" +
                                " WHERE mr.member.id = :memberId" +
                                " AND mr.room.id = :roomId" +
                                " AND mr.room.deleted = FALSE", MemberRoom.class)
                .setParameter("memberId", memberId)
                .setParameter("roomId", roomId)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<Room> findParticipatingRoomsByMemberIdWithCursor(UUID memberId, Long cursor, int limit) {
        return em.createQuery(
                        "SELECT mr.room" +
                                " FROM MemberRoom mr" +
                                " WHERE mr.member.id = :memberId" +
                                ((cursor == null) ? "" : " AND mr.room.id < " + cursor) +
                                " ORDER BY mr.room.id DESC", Room.class)
                .setParameter("memberId", memberId)
                .setMaxResults(limit)
                .getResultList();
    }


    @Override
    public List<Room> findAllParticipatingRooms(UUID memberId) {
        return em.createQuery(
                        "SELECT mr.room" +
                                " FROM MemberRoom mr" +
                                " WHERE mr.member.id = :memberId" +
                                " AND mr.room.deleted = FALSE" +
                                " ORDER BY mr.room.id DESC", Room.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
    @Override
    public List<Room> findRoomsByState(RoomState state) {
        return em.createQuery(
                        "SELECT DISTINCT r" +
                                " FROM Room r" +
                                " JOIN FETCH r.memberRooms" +
                                " WHERE r.state = :roomState" +
                                " AND r.deleted = FALSE", Room.class)
                .setParameter("roomState", state)
                .getResultList();
    }

    @Override
    public Optional<Relation> findRelationByManittoId(UUID memberId, Long roomId) {
        return em.createQuery(
                        "SELECT r" +
                                " FROM Relation r" +
                                " WHERE r.manitto.id = :memberId" +
                                " AND r.room.id = :roomId" +
                                " AND r.room.deleted = FALSE", Relation.class)
                .setParameter("memberId", memberId)
                .setParameter("roomId", roomId)
                .getResultStream()
                .findFirst();
    }
}
