package com.firefighter.aenitto.missions.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.firefighter.aenitto.missions.domain.CommonMission;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class DailyCommonMissionResponse {
	private final String mission;

	public static DailyCommonMissionResponse of(CommonMission commonMission) {
		return new DailyCommonMissionResponse(commonMission.getMission().getContent());
	}

}
