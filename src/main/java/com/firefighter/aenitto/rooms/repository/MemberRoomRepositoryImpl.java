package com.firefighter.aenitto.rooms.repository;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.firefighter.aenitto.rooms.domain.MemberRoom;

@Repository
@Qualifier("memberRoomRepositoryImpl")
@RequiredArgsConstructor
public class
MemberRoomRepositoryImpl implements MemberRoomRepository {
	private final EntityManager em;

	@Override
	public void delete(MemberRoom memberRoom) {
		em.remove(memberRoom);
	}
}
