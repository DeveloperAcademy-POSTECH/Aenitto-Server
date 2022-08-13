package com.firefighter.aenitto.missions;

import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;
import org.springframework.test.util.ReflectionTestUtils;

public class MissionFixture {

    private static Mission baseMissionFixture(int number, MissionType type) {
        Mission mission = transientMissionFixture(number, type);
        ReflectionTestUtils.setField(mission, "id", number * 1L);
        return mission;
    }
    public static Mission transientMissionFixture(int number, MissionType type) {
        return Mission.builder()
                .content(type.getValue() + "미션" + number)
                .type(type)
                .build();
    }
}
