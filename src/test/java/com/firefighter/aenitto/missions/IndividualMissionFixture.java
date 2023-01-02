package com.firefighter.aenitto.missions;

import java.time.LocalDate;

import org.springframework.test.util.ReflectionTestUtils;

import com.firefighter.aenitto.missions.domain.IndividualMission;

public class IndividualMissionFixture {
	public static IndividualMission individualMissionFixture1() {
		return baseIndividualMissionFixture(1);
	}

	private static IndividualMission baseIndividualMissionFixture(int number) {
		IndividualMission individualMission = transientIndividualMissionFixture();
		ReflectionTestUtils.setField(individualMission, "id", 1L * number);
		ReflectionTestUtils.setField(individualMission, "date", LocalDate.now().minusDays(number - 1));
		return individualMission;
	}

	public static IndividualMission transientIndividualMissionFixture() {
		return IndividualMission.builder().build();
	}
}
