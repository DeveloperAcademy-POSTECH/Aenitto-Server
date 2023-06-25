package com.firefighter.aenitto.missions.service;

import com.firefighter.aenitto.missions.domain.IndividualMission;
import com.firefighter.aenitto.missions.dto.response.UpdateRequest;
import com.firefighter.aenitto.missions.dto.response.UpdateResponse;
import com.firefighter.aenitto.rooms.repository.MemberRoomRepository;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.firefighter.aenitto.common.exception.mission.MissionAlreadySetException;
import com.firefighter.aenitto.common.exception.mission.MissionEmptyException;
import com.firefighter.aenitto.common.exception.mission.MissionNotFoundException;
import com.firefighter.aenitto.missions.domain.CommonMission;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;
import com.firefighter.aenitto.missions.dto.response.DailyCommonMissionResponse;
import com.firefighter.aenitto.missions.repository.CommonMissionRepository;
import com.firefighter.aenitto.missions.repository.MissionRepository;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;
import com.firefighter.aenitto.rooms.repository.RoomRepository;

@Service
@Qualifier("missionServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService {

  @Qualifier("roomRepositoryImpl")
  private final RoomRepository roomRepository;

  @Qualifier("missionRepositoryImpl")
  private final MissionRepository missionRepository;

  @Qualifier("commonMissionRepositoryImpl")
  private final CommonMissionRepository commonMissionRepository;

  private final MemberRoomRepository memberRoomRepository;

  @Override
  @Transactional
  public Long setDailyCommonMission(LocalDate date)
      throws MissionAlreadySetException, MissionEmptyException {
    // 해당 일자에 CommonMission 이 이미 있다면 -> throw
    commonMissionRepository.findCommonMissionByDate(date)
        .ifPresent(m -> {
          throw new MissionAlreadySetException();
        });

    // 랜덤한 Mission 을 하나 가져오고, 아예 없을 경우 -> throw
    Mission mission = missionRepository.findRandomMission(MissionType.COMMON)
        .orElseThrow(MissionEmptyException::new);

    return commonMissionRepository.saveCommonMission(
        CommonMission.createCommonMission(date, mission)).getId();
  }

  @Override
  public DailyCommonMissionResponse getDailyCommonMission() throws MissionNotFoundException {
    CommonMission commonMission = commonMissionRepository.findCommonMissionByDate(LocalDate.now())
        .orElseThrow(MissionNotFoundException::new);
    return DailyCommonMissionResponse.of(commonMission);
  }

  @Override
  @Transactional
  public UpdateResponse updateIndividualMission(Long memberRoomId, UpdateRequest dto) {
    Mission mission = new Mission(dto.getMission(), MissionType.INDIVIDUAL);
    missionRepository.save(mission);
    missionRepository.findIndividualMissionByDate(LocalDate.now(), memberRoomId)
        .ifPresent((individualMission -> individualMission.changeMission(mission)));
    return UpdateResponse.fromEntity(mission);
  }

  @Override
  @Transactional
  public void setDailyIndividualMission(LocalDate date)
      throws MissionAlreadySetException, MissionEmptyException {
    List<Room> roomsProcessing = roomRepository.findRoomsByState(RoomState.PROCESSING);
    for (Room room : roomsProcessing) {
      Mission mission = missionRepository.findRandomMission(MissionType.INDIVIDUAL)
          .orElseThrow(MissionEmptyException::new);
      for (MemberRoom memberRoom : room.getMemberRooms()) {
        if (memberRoom.didSetDailyIndividualMission(date)) {
          throw new MissionAlreadySetException();
        }
        memberRoom.addIndividualMission(mission, date);
      }
    }
  }

  @Override
  public void setInitialIndividualMission(MemberRoom memberRoom) {
    Mission mission = missionRepository.findRandomMission(MissionType.INDIVIDUAL)
        .orElseThrow(MissionEmptyException::new);
    memberRoom.addIndividualMission(mission, LocalDate.now());
  }
}
