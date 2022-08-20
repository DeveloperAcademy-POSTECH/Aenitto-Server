package com.firefighter.aenitto.missions.service;

import com.firefighter.aenitto.common.exception.mission.MissionNotFoundException;
import com.firefighter.aenitto.missions.domain.MissionType;
import com.firefighter.aenitto.missions.dto.response.DailyCommonMissionResponse;

import java.time.LocalDate;

public interface MissionService {
    Long setDailyCommonMission(LocalDate date);
    void setDailyIndividualMission(LocalDate date);
    DailyCommonMissionResponse getDailyCommonMission() throws MissionNotFoundException;
}
