package com.firefighter.aenitto.missions.service;

import com.firefighter.aenitto.common.exception.mission.MissionNotFoundException;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.missions.dto.response.DailyCommonMissionResponse;
import com.firefighter.aenitto.missions.dto.response.UpdateRequest;
import com.firefighter.aenitto.missions.dto.response.UpdateResponse;
import com.firefighter.aenitto.rooms.domain.Room;
import java.time.LocalDate;

public interface MissionService {

  Long setDailyCommonMission(LocalDate date);

  void setDailyIndividualMission(LocalDate date);

  void setInitialIndividualMission(Room room);

  DailyCommonMissionResponse getDailyCommonMission() throws MissionNotFoundException;

  UpdateResponse updateIndividualMission(Member member, Long roomId, UpdateRequest dto);

  UpdateResponse restoreIndividualMission(Member member, Long roomId);
}
