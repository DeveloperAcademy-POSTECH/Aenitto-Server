package com.firefighter.aenitto.rooms.repository;


import com.firefighter.aenitto.rooms.domain.Relation;

import java.util.Optional;
import java.util.UUID;

public interface RelationRepository {
    public Optional<Relation> findByRoomIdAndMemberId(Long roomId, UUID memberId);
}
