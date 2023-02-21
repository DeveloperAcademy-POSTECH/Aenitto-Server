package com.firefighter.aenitto.missions;

import org.springframework.test.util.ReflectionTestUtils;

import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;

public class MissionFixture {
	public static Mission missionFixture1_Common() {
		return baseMissionFixture(1, MissionType.COMMON);
	}

	public static Mission missionFixture2_Individual() {
		return baseMissionFixture(2, MissionType.INDIVIDUAL);
	}

	private static Mission baseMissionFixture(int number, MissionType type) {
		Mission mission = transientMissionFixture(number, type);
		ReflectionTestUtils.setField(mission, "id", number * 1L);
		ReflectionTestUtils.setField(mission, "content", "string");
		return mission;
	}

	public static Mission transientMissionFixture(int number, MissionType type) {
		return Mission.builder()
			.content(type.getValue() + "미션" + number)
			.type(type)
			.build();
	}
}
