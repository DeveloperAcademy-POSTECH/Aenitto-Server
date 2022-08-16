package com.firefighter.aenitto.missions;

import com.firefighter.aenitto.missions.domain.CommonMission;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

public class CommonMissionFixture {
    public static CommonMission commonMissionFixture1() {
        return baseCommonMissionFixture(1);
    }


    private static CommonMission baseCommonMissionFixture(int number) {
        CommonMission commonMission = transientCommonMissionFixture();
        ReflectionTestUtils.setField(commonMission, "id", number * 1L);
        ReflectionTestUtils.setField(commonMission, "date", LocalDate.now().minusDays(number - 1));
        return commonMission;
    }

    public static CommonMission transientCommonMissionFixture() {
        return CommonMission.builder().build();
    }
}