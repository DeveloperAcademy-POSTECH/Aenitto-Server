package com.firefighter.aenitto.rooms.repository;


import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository {
    public Room saveRoom(Room room);
    public Room mergeRoom(Room room);

    public List<Room> findAllRooms();
    public Optional<Room> findRoomById(Long id);
    public Optional<Room> findByInvitation(String invitation);
    public Optional<MemberRoom> findMemberRoomById(UUID memberId, Long roomId);
    public List<Room> findParticipatingRoomsByMemberIdWithCursor(UUID memberId, Long cursor, int limit);

    public List<Room> findAllParticipatingRooms(UUID memberId);
    public List<Room> findRoomsByState(RoomState state);

    public Optional<Relation> findRelationByManittoId(UUID memberId, Long roomId);
    public List<Room> findRoomsByStateAndEndDate(RoomState state, LocalDate endDate);
}
