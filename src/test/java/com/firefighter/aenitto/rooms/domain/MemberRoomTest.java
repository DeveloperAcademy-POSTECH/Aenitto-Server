package com.firefighter.aenitto.rooms.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.firefighter.aenitto.missions.MissionFixture;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;
import com.firefighter.aenitto.missions.repository.MissionRepositoryImpl;
import com.firefighter.aenitto.rooms.RoomFixture;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class MemberRoomTest {
	@Autowired
	EntityManager em;
	@Autowired
	MissionRepositoryImpl missionRepository;

	MemberRoom memberRoom1;
	MemberRoom memberRoom2;

	Mission mission1;

	@BeforeEach
	void init() {
		memberRoom1 = RoomFixture.transientMemberRoomFixture(1);
		memberRoom2 = RoomFixture.transientMemberRoomFixture(2);

		mission1 = MissionFixture.transientMissionFixture(1, MissionType.INDIVIDUAL);
	}

	@DisplayName("addIndividualMission 메서드")
	@Test
	void addIndividualMission_success() {
		em.persist(memberRoom1);
		em.persist(mission1);

		em.flush();
		em.clear();

		// when
		MemberRoom memberRoom = em.find(MemberRoom.class, memberRoom1.getId());
		Mission mission = missionRepository.findRandomMission(MissionType.INDIVIDUAL)
			.orElseThrow(RuntimeException::new);

		memberRoom.addIndividualMission(mission, LocalDate.now());

		em.flush();
		em.clear();

		MemberRoom findMemberRoom = em.find(MemberRoom.class, memberRoom.getId());

		// then
		assertThat(findMemberRoom.getIndividualMissions()).hasSize(1);
		assertThat(findMemberRoom.getIndividualMissions().get(0).getMission().getContent()).isEqualTo(
			mission.getContent());
	}

	@DisplayName("didSetDailyIndividualMission 메서드")
	@Test
	void didSetDailyIndividualMission_success() {
		// given
		em.persist(mission1);
		em.persist(memberRoom1);
		em.persist(memberRoom2);

		em.flush();
		em.clear();

		MemberRoom memberRoom = em.find(MemberRoom.class, memberRoom1.getId());
		Mission mission = em.find(Mission.class, mission1.getId());

		// when
		memberRoom.addIndividualMission(mission, LocalDate.now());

		em.flush();
		em.clear();

		MemberRoom fmr1 = em.find(MemberRoom.class, memberRoom1.getId());
		MemberRoom fmr2 = em.find(MemberRoom.class, memberRoom2.getId());

		// then
		assertThat(fmr1.didSetDailyIndividualMission(LocalDate.now())).isTrue();
		assertThat(fmr2.didSetDailyIndividualMission(LocalDate.now())).isFalse();
	}
}
