package com.firefighter.aenitto.rooms.repository;

import com.firefighter.aenitto.members.MemberFixture;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.rooms.RoomFixture;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Room;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class MemberRoomRepositoryTest {
    @Autowired
    EntityManager em;

    @Autowired
    @Qualifier("memberRoomRepositoryImpl")
    MemberRoomRepository memberRoomRepository;

    @Test
    @DisplayName("delete 테스트 - 성공")
    void delete_success() {
        // given
        Member member1 = MemberFixture.transientMemberFixture(1);
        Member member2 = MemberFixture.transientMemberFixture(2);
        Member member3 = MemberFixture.transientMemberFixture(3);

        Room room1 = RoomFixture.transientRoomFixture(1, 10, 10);

        MemberRoom memberRoom1 = RoomFixture.transientMemberRoomFixture(1);
        MemberRoom memberRoom2 = RoomFixture.transientMemberRoomFixture(2);
        MemberRoom memberRoom3 = RoomFixture.transientMemberRoomFixture(3);
        memberRoom1.setMemberRoom(member1, room1);
        memberRoom2.setMemberRoom(member2, room1);
        memberRoom3.setMemberRoom(member3, room1);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(room1);

        em.flush();
        em.clear();

        // when
        MemberRoom findMemberRoom1 = em.find(MemberRoom.class, memberRoom1.getId());
        memberRoomRepository.delete(findMemberRoom1);

        em.flush();
        em.clear();

        Room room = em.find(Room.class, room1.getId());

        // then
        assertThat(room.getMemberRooms()).hasSize(2);
    }

}
