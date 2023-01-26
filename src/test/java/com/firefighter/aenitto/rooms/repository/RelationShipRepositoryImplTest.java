package com.firefighter.aenitto.rooms.repository;

import com.firefighter.aenitto.common.utils.SqlPath;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.firefighter.aenitto.members.MemberFixture.memberFixture;
import static com.firefighter.aenitto.members.MemberFixture.memberFixture2;
import static com.firefighter.aenitto.rooms.RoomFixture.roomFixture1;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class RelationShipRepositoryImplTest {
	@Autowired
	EntityManager em;

	@Autowired
	RelationRepositoryImpl target;

	@Autowired
	RoomRepositoryImpl roomRepository;

	Member manitto;
	Member manittee;
	Room room;

	@BeforeEach
	void setUp() {
		manittee = memberFixture();
		manitto = memberFixture2();
		room = roomFixture1();
	}

	@DisplayName("roomId 와 manittoId로 relation찾기 - 성공")
	@Test()
	@Sql("classpath:relation.sql")
	void findByRoomIdAndManitto() {
		// when
		final Optional<Relation> result = target.findByRoomIdAndManittoId(100L,
			UUID.fromString("a383cdb3-a871-4410-b146-fb1f7b447b9e"));

		// then
		assertThat(result).isNotNull();
		assertThat(result.get().getId()).isEqualTo(1L);
		assertThat(result.get().getRoom().getId()).isEqualTo(100L);
		assertThat(result.get().getManittee().getNickname()).isEqualTo("manittee");
	}

	@DisplayName("roomId 와 manitteeId로 relation찾기 - 성공")
	@Test()
	@Sql({
		SqlPath.MEMBER,
		SqlPath.ROOM_PROCESSING,
		SqlPath.RELATION
	})
	void findByRoomIdAndManittee() {
		// when
		final Optional<Relation> result = target.findByRoomIdAndManitteeId(2L,
			UUID.fromString("4ba90eab-c62b-4d44-aadb-b7f3183ea83e"));

		// then
		assertThat(result).isNotNull();
		assertThat(result.get().getId()).isEqualTo(1L);
		assertThat(result.get().getRoom().getId()).isEqualTo(2L);
		assertThat(result.get().getManittee().getNickname()).isEqualTo("nickname2");
	}

	@DisplayName("진행중인 방 중 마니또 마치는 날인 방 가져오기 - 성공")
	@Sql(
		SqlPath.ROOM_PROCESSING
	)
	@Test
	void findRoomsByStateAndEndDate_success() {

		// when
		// List<Room> foundRoom = roomRepository.findRoomsByStateAndEndDate(RoomState.PROCESSING, LocalDate.now());
		// Optional<Room> foundRoom = roomRepository.findRoomById(2L);
		// List<Room> foundRoom = roomRepository.findAllRooms();
		List<Room> foundRoom = roomRepository.findRoomsByState(RoomState.PROCESSING);

		// then
		assertThat(foundRoom).isNotNull();
		assertThat(foundRoom.get(0).getState()).isEqualTo(RoomState.PROCESSING);
		// assertThat(foundRoom.get(0).getState()).isEqualTo(RoomState.PROCESSING);
		// assertThat(foundRoom.get(0).getEndDate()).isEqualTo(LocalDate.now());
	}

}
