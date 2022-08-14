package com.firefighter.aenitto.missions;

import com.firefighter.aenitto.missions.domain.CommonMission;
import org.springframework.test.util.ReflectionTestUtils;

public class CommonMissionFixture {

    private static CommonMission baseCommonMissionFixture(int number) {
        CommonMission commonMission = transientCommonMissionFixture();
        ReflectionTestUtils.setField(commonMission, "id", number * 1L);
        return commonMission;
    }

    public static CommonMission transientCommonMissionFixture() {
        return CommonMission.builder().build();
    }
}
