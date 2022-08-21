package com.firefighter.aenitto.missions.integration;

import com.firefighter.aenitto.common.exception.mission.MissionErrorCode;
import com.firefighter.aenitto.common.utils.SqlPath;
import com.firefighter.aenitto.support.IntegrationTest;
import com.firefighter.aenitto.support.security.WithMockCustomMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockCustomMember
public class CommonMissionControllerIntegrationTest extends IntegrationTest {

    @DisplayName("오늘의 공통 미션 -> 성공")
    @Sql( SqlPath.COMMON_MISSION )
    @Test
    void getDailyCommonMission_success() throws Exception {
        // given
        final String url = "/api/v1/missions/common";

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mission").exists());
    }

    @DisplayName("오늘의 공통 미션 -> 실패 / 오늘의 공통미션이 없음")
    @Test
    void getDailyCommonMission_failure() throws Exception {
        // given
        final String url = "/api/v1/missions/common";

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(MissionErrorCode.MISSION_NOT_FOUND.getStatus().value())))
                .andExpect(jsonPath("$.message", is(MissionErrorCode.MISSION_NOT_FOUND.getMessage())));
    }
}
