package com.firefighter.aenitto.rooms.repository;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.firefighter.aenitto.rooms.domain.Relation;

@RequiredArgsConstructor
@Repository
public class RelationRepositoryImpl implements RelationRepository {
	private final EntityManager em;

	@Override
	public Optional<Relation> findByRoomIdAndManittoId(Long roomId, UUID memberId) {
		return em.createQuery(
				"SELECT r" +
					" FROM Relation r" +
					" WHERE r.room.id = :roomId" +
					" AND r.manitto.id = :memberId", Relation.class)
			.setParameter("roomId", roomId)
			.setParameter("memberId", memberId)
			.getResultList().stream().findFirst();
	}

	@Override
	public Optional<Relation> findByRoomIdAndManitteeId(Long roomId, UUID memberId) {
		return em.createQuery(
				"SELECT r" +
					" FROM Relation r" +
					" WHERE r.room.id = :roomId" +
					" AND r.manittee.id = :memberId", Relation.class)
			.setParameter("roomId", roomId)
			.setParameter("memberId", memberId)
			.getResultList().stream().findFirst();
	}
}
