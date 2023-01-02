package com.firefighter.aenitto.missions.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import static com.firefighter.aenitto.missions.CommonMissionFixture.*;
import static com.firefighter.aenitto.missions.MissionFixture.*;
import static com.firefighter.aenitto.rooms.RoomFixture.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.firefighter.aenitto.common.exception.mission.MissionAlreadySetException;
import com.firefighter.aenitto.common.exception.mission.MissionEmptyException;
import com.firefighter.aenitto.common.exception.mission.MissionNotFoundException;
import com.firefighter.aenitto.members.MemberFixture;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.missions.domain.CommonMission;
import com.firefighter.aenitto.missions.domain.IndividualMission;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;
import com.firefighter.aenitto.missions.dto.response.DailyCommonMissionResponse;
import com.firefighter.aenitto.missions.repository.CommonMissionRepositoryImpl;
import com.firefighter.aenitto.missions.repository.MissionRepositoryImpl;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;
import com.firefighter.aenitto.rooms.repository.RoomRepositoryImpl;

@ExtendWith(MockitoExtension.class)
public class MissionServiceTest {
	@InjectMocks
	private MissionServiceImpl missionService;

	@Mock
	private MissionRepositoryImpl missionRepository;

	@Mock
	private CommonMissionRepositoryImpl commonMissionRepository;

	@Mock
	private RoomRepositoryImpl roomRepository;

	Mission mission1_common;
	Mission mission2_individual;
	CommonMission commonMission1;
	IndividualMission individualMission1;

	Member member1;
	Member member2;
	Member member3;
	Member member4;
	Member member5;

	Room room1;
	Room room2;

	MemberRoom memberRoom1;
	MemberRoom memberRoom2;
	MemberRoom memberRoom3;
	MemberRoom memberRoom4;
	MemberRoom memberRoom5;

	@BeforeEach
	void init() {
		mission1_common = missionFixture1_Common();
		mission2_individual = missionFixture2_Individual();
		commonMission1 = commonMissionFixture1();

		room1 = roomFixture1();
		room2 = roomFixture2();

		member1 = MemberFixture.memberFixture();
		member2 = MemberFixture.memberFixture2();
		member3 = MemberFixture.memberFixture3();
		member4 = MemberFixture.memberFixture4();
		member5 = MemberFixture.memberFixture5();

		memberRoom1 = memberRoomFixture1(member1, room1);
		memberRoom2 = memberRoomFixture1(member2, room1);
		memberRoom3 = memberRoomFixture1(member3, room1);
		memberRoom4 = memberRoomFixture1(member4, room2);
		memberRoom5 = memberRoomFixture1(member5, room2);
	}

	@DisplayName("해당 일자의 CommonMission 세팅 - 실패 (해당 일자에 CommonMission 이 이미 존재)")
	@Test
	void setDailyCommonMission_fail_already_exist() {
		// when
		when(commonMissionRepository.findCommonMissionByDate(any(LocalDate.class)))
			.thenReturn(Optional.of(commonMission1));

		// then
		assertThatExceptionOfType(MissionAlreadySetException.class)
			.isThrownBy(() -> {
				missionService.setDailyCommonMission(LocalDate.now());
			});
	}

	@DisplayName("해당 일자의 CommonMission 세팅 - 실패 (Mission 테이블이 비어있음)")
	@Test
	void setDailyCommonMission_fail_mission_empty() {
		// when
		when(commonMissionRepository.findCommonMissionByDate(any(LocalDate.class)))
			.thenReturn(Optional.empty());
		when(missionRepository.findRandomMission(any(MissionType.class)))
			.thenReturn(Optional.empty());

		// then
		assertThatExceptionOfType(MissionEmptyException.class)
			.isThrownBy(() -> {
				missionService.setDailyCommonMission(LocalDate.now());
			});
	}

	@DisplayName("해당 일자의 CommonMission 세팅 - 성공")
	@Test
	void setDailyCommonMission_success() {
		// given

		// when
		when(commonMissionRepository.findCommonMissionByDate(any(LocalDate.class)))
			.thenReturn(Optional.empty());
		when(missionRepository.findRandomMission(any(MissionType.class)))
			.thenReturn(Optional.of(mission1_common));
		when(commonMissionRepository.saveCommonMission(any(CommonMission.class)))
			.thenReturn(commonMission1);

		Long commonMissionId = missionService.setDailyCommonMission(LocalDate.now());

		// then
		assertThat(commonMissionId).isEqualTo(commonMission1.getId());
	}

	@DisplayName("해당 일자의 IndividualMission 세팅 - 실패 (미션 테이블이 비어있음)")
	@Test
	void setDailyIndividualMission_fail_already_exist() {
		// given
		List<Room> roomList = new ArrayList<>();
		roomList.add(room1);

		// when
		when(roomRepository.findRoomsByState(any(RoomState.class)))
			.thenReturn(roomList);
		when(missionRepository.findRandomMission(any(MissionType.class)))
			.thenReturn(Optional.empty());

		// then
		assertThatExceptionOfType(MissionEmptyException.class)
			.isThrownBy(() -> {
				missionService.setDailyIndividualMission(LocalDate.now());
			});
	}

	@DisplayName("해당 일자의 IndividualMission 세팅 - 실패 (해당 일자에 미션이 이미 있음)")
	@Test
	void setDailyIndividualMission_fail_mission_empty() {
		// given
		List<Room> roomList = new ArrayList<>();
		roomList.add(room1);
		memberRoom2.addIndividualMission(mission2_individual, LocalDate.now());

		// when
		when(roomRepository.findRoomsByState(any(RoomState.class)))
			.thenReturn(roomList);
		when(missionRepository.findRandomMission(any(MissionType.class)))
			.thenReturn(Optional.of(mission2_individual));

		// then
		assertThatExceptionOfType(MissionAlreadySetException.class)
			.isThrownBy(() -> {
				missionService.setDailyIndividualMission(LocalDate.now());
			});
	}

	@DisplayName("해당 일자의 IndividualMission 세팅 - 성공")
	@Test
	void setDailyIndividualMission_fail_success() {
		// given
		List<Room> roomList = new ArrayList<>();
		roomList.add(room1);

		// when
		when(roomRepository.findRoomsByState(any(RoomState.class)))
			.thenReturn(roomList);
		when(missionRepository.findRandomMission(any(MissionType.class)))
			.thenReturn(Optional.of(mission2_individual));

		missionService.setDailyIndividualMission(LocalDate.now());

		// then
		assertThat(memberRoom1.didSetDailyIndividualMission(LocalDate.now())).isTrue();
		assertThat(memberRoom2.didSetDailyIndividualMission(LocalDate.now())).isTrue();
		assertThat(memberRoom3.didSetDailyIndividualMission(LocalDate.now())).isTrue();
	}

	@DisplayName("해당 일자의 공통 미션 가져오기 - 성공")
	@Test
	void getDailyCommonMission_success() {

		//given
		ReflectionTestUtils.setField(commonMission1, "mission", mission1_common);

		//when
		when(commonMissionRepository.findCommonMissionByDate(any(LocalDate.class)))
			.thenReturn(Optional.of(commonMission1));
		DailyCommonMissionResponse result =
			missionService.getDailyCommonMission();

		//then
		assertThat(result.getMission()).isEqualTo(commonMission1.getMission().getContent());
		verify(commonMissionRepository, times(1))
			.findCommonMissionByDate(any(LocalDate.class));
	}

	@DisplayName("해당 일자의 공통 미션 가져오기 - 실패")
	@Test
	void getDailyCommonMission_fail_no_common_mission() {
		//when
		when(commonMissionRepository.findCommonMissionByDate(any(LocalDate.class)))
			.thenReturn(Optional.empty());

		//then
		assertThatExceptionOfType(MissionNotFoundException.class)
			.isThrownBy(() -> {
				missionService.getDailyCommonMission();
			});
	}
}
