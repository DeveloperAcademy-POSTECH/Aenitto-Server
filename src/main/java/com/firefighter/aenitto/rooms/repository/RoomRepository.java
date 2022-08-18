package com.firefighter.aenitto.rooms.repository;


import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;

import java.util.List;
import java.util.UUID;

public interface RoomRepository {
    public Room saveRoom(Room room);
    public Room mergeRoom(Room room);
    public Room findRoomById(Long id);
    public Room findByInvitation(String invitation);
    public MemberRoom findMemberRoomById(UUID memberId, Long roomId);
    public List<Room> findParticipatingRoomsByMemberIdWithCursor(UUID memberId, Long cursor, int limit);
    public List<Room> findRoomsByState(RoomState state);
}
