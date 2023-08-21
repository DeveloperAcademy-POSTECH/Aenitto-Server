package com.firefighter.aenitto.rooms.repository;

import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, Long> {

  @Query("SELECT r FROM Room r WHERE r.invitation = :invitation AND r.deleted = FALSE")
  Optional<Room> findByInvitation(String invitation);

  @Query("SELECT mr.room FROm MemberRoom mr WHERE mr.member.id = :memberId AND mr.room.deleted = FALSE ORDER BY mr.room.startDate ASC, mr.room.id DESC")
  List<Room> findAllParticipatingRooms(UUID memberId);

  @Query("SELECT DISTINCT r FROM Room r JOIN FETCH r.memberRooms WHERE r.state = :state AND r.deleted = FALSE")
  List<Room> findRoomsByState(RoomState state);
}
