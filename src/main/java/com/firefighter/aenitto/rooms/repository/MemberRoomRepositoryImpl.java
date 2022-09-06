package com.firefighter.aenitto.rooms.repository;

import com.firefighter.aenitto.rooms.domain.MemberRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@Qualifier("memberRoomRepositoryImpl")
@RequiredArgsConstructor
public class MemberRoomRepositoryImpl implements MemberRoomRepository {
    private final EntityManager em;

    @Override
    public void delete(MemberRoom memberRoom) {
        em.remove(memberRoom);
    }
}
