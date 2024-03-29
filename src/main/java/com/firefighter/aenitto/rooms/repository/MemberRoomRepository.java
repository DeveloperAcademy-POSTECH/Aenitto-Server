package com.firefighter.aenitto.rooms.repository;

import com.firefighter.aenitto.rooms.domain.MemberRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRoomRepository extends JpaRepository<MemberRoom, Long> {
  Optional<MemberRoom> findByMemberIdAndRoomId(UUID memberId, Long roomId);
}
