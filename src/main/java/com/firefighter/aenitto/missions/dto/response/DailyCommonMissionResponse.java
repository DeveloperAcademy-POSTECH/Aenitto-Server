package com.firefighter.aenitto.missions.dto.response;

import com.firefighter.aenitto.missions.domain.CommonMission;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.dto.response.ParticipatingRoomsResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class DailyCommonMissionResponse {
    private final String mission;

    public static DailyCommonMissionResponse of(CommonMission commonMission) {
        return new DailyCommonMissionResponse(commonMission.getMission().getContent());
    }

}
