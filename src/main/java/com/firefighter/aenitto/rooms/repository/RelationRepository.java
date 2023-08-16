package com.firefighter.aenitto.rooms.repository;

import com.firefighter.aenitto.rooms.domain.Relation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RelationRepository extends JpaRepository<Relation, Long> {
  Optional<Relation> findByRoomIdAndManittoId(Long roomId, UUID memberId);

  Optional<Relation> findByRoomIdAndManitteeId(Long roomId, UUID memberId);
}
