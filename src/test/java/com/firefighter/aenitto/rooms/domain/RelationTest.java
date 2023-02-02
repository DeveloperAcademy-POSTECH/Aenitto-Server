package com.firefighter.aenitto.rooms.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.firefighter.aenitto.members.MemberFixture;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.rooms.RoomFixture;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class RelationTest {
	@Autowired
	private EntityManager em;

	Room room1;
	List<Member> members = new ArrayList<>();
	List<MemberRoom> memberRooms = new ArrayList<>();

	@BeforeEach
	void initTest() {
		room1 = RoomFixture.transientRoomFixture(1, 10, 10);
		for (int i = 0; i < 7; i++) {
			Member member = MemberFixture.transientMemberFixture(i);
			MemberRoom memberRoom = RoomFixture.transientMemberRoomFixture(i);
			memberRoom.setMemberRoom(member, room1);
			members.add(member);
			memberRooms.add(memberRoom);
		}
	}

	@DisplayName("마니또 관계 생성 테스트 - 성공")
	@Test
	void startRoulette_success() {
		// given
		members.stream().forEach((member -> em.persist(member)));
		em.persist(room1);

		em.flush();
		em.clear();

		// given 2
		Room findRoom = em.find(Room.class, room1.getId());
		Relation.createRelations(findRoom.getMemberRooms(), findRoom);

		em.flush();
		em.clear();

		// when
		Room findRoom2 = em.find(Room.class, room1.getId());

		// then
		assertThat(findRoom2.getRelations().size()).isEqualTo(7);
	}
}
