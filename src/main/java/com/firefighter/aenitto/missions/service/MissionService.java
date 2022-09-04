package com.firefighter.aenitto.missions.service;

import com.firefighter.aenitto.common.exception.mission.MissionNotFoundException;
import com.firefighter.aenitto.missions.domain.MissionType;
import com.firefighter.aenitto.missions.dto.response.DailyCommonMissionResponse;
import com.firefighter.aenitto.rooms.domain.MemberRoom;

import java.time.LocalDate;

public interface MissionService {
    Long setDailyCommonMission(LocalDate date);
    void setDailyIndividualMission(LocalDate date);
    void setInitialIndividualMission(MemberRoom memberRoom);
    DailyCommonMissionResponse getDailyCommonMission() throws MissionNotFoundException;
}
