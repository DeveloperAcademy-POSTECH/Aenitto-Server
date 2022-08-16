package com.firefighter.aenitto.missions.service;

import com.firefighter.aenitto.common.exception.mission.MissionAlreadySetException;
import com.firefighter.aenitto.common.exception.mission.MissionEmptyException;
import com.firefighter.aenitto.missions.domain.CommonMission;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;
import com.firefighter.aenitto.missions.repository.MissionRepository;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;
import com.firefighter.aenitto.rooms.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Qualifier("missionServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService {
    @Qualifier("roomRepositoryImpl")
    private final RoomRepository roomRepository;

    @Qualifier("missionRepositoryImpl")
    private final MissionRepository missionRepository;

    @Override
    @Transactional
    public Long setDailyCommonMission(LocalDate date) throws MissionAlreadySetException, MissionEmptyException {
        // 해당 일자에 CommonMission 이 이미 있다면 -> throw
        missionRepository.findCommonMissionByDate(date)
                .ifPresent(m -> {
                    throw new MissionAlreadySetException();
                });

        // 랜덤한 Mission 을 하나 가져오고, 아예 없을 경우 -> throw
        Mission mission = missionRepository.findRandomMission(MissionType.COMMON)
                .orElseThrow(MissionEmptyException::new);

        return missionRepository.saveCommonMission(CommonMission.createCommonMission(date, mission)).getId();
    }

    @Override
    @Transactional
    public void setDailyIndividualMission(LocalDate date) throws MissionAlreadySetException, MissionEmptyException {
        List<Room> roomsProcessing = roomRepository.findRoomsByState(RoomState.PROCESSING);
        for (Room room : roomsProcessing) {
            for (MemberRoom memberRoom : room.getMemberRooms()) {
                if (memberRoom.didSetDailyIndividualMission(date)) {
                    throw new MissionAlreadySetException();
                }
                Mission mission = missionRepository.findRandomMission(MissionType.INDIVIDUAL)
                        .orElseThrow(MissionEmptyException::new);
                memberRoom.addIndividualMission(mission, date);
            }
        }
    }
}