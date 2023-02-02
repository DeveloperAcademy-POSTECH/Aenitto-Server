package com.firefighter.aenitto.missions.service;

import java.time.LocalDate;

import com.firefighter.aenitto.common.exception.mission.MissionNotFoundException;
import com.firefighter.aenitto.missions.dto.response.DailyCommonMissionResponse;
import com.firefighter.aenitto.rooms.domain.MemberRoom;

public interface MissionService {
	Long setDailyCommonMission(LocalDate date);

	void setDailyIndividualMission(LocalDate date);

	void setInitialIndividualMission(MemberRoom memberRoom);

	DailyCommonMissionResponse getDailyCommonMission() throws MissionNotFoundException;
}
