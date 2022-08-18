package com.firefighter.aenitto.missions.repository;

import com.firefighter.aenitto.missions.domain.CommonMission;
import com.firefighter.aenitto.missions.domain.IndividualMission;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;
import com.firefighter.aenitto.rooms.domain.MemberRoom;

import java.time.LocalDate;
import java.util.Optional;

public interface MissionRepository {
    CommonMission saveCommonMission(CommonMission commonMission);
    Optional<Mission> findRandomMission(MissionType missionType);

    Optional<CommonMission> findCommonMissionByDate(LocalDate date);

    Optional<IndividualMission> findIndividualMissionByDate(LocalDate date, Long memberRoomId);
}
