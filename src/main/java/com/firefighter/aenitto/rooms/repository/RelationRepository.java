package com.firefighter.aenitto.rooms.repository;

import java.util.Optional;
import java.util.UUID;

import com.firefighter.aenitto.rooms.domain.Relation;

public interface RelationRepository {
	Optional<Relation> findByRoomIdAndManittoId(Long roomId, UUID memberId);

	Optional<Relation> findByRoomIdAndManitteeId(Long roomId, UUID memberId);
}
