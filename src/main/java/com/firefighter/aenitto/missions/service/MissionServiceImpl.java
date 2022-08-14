package com.firefighter.aenitto.missions.service;

import com.firefighter.aenitto.common.exception.mission.MissionAlreadySetException;
import com.firefighter.aenitto.common.exception.mission.MissionEmptyException;
import com.firefighter.aenitto.missions.domain.CommonMission;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;
import com.firefighter.aenitto.missions.repository.MissionRepository;
import com.firefighter.aenitto.rooms.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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
        // Individual mission
        // Processing 인 모든 Room 가져옴 -> Room iter하면서, MemberRoom iter하면서 random으로 individial mission 등록
    }
}
