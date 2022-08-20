package com.firefighter.aenitto.missions.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefighter.aenitto.common.exception.GlobalExceptionHandler;
import com.firefighter.aenitto.common.exception.mission.MissionErrorCode;
import com.firefighter.aenitto.common.exception.mission.MissionNotFoundException;
import com.firefighter.aenitto.common.exception.room.RoomErrorCode;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.missions.domain.CommonMission;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.dto.response.DailyCommonMissionResponse;
import com.firefighter.aenitto.missions.service.MissionService;
import com.firefighter.aenitto.rooms.dto.RoomResponseDtoBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static com.firefighter.aenitto.missions.CommonMissionFixture.commonMissionFixture1;
import static com.firefighter.aenitto.missions.MissionFixture.missionFixture1_Common;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@AutoConfigureRestDocs
public class CommonMissionControllerTest {
    public static final String AUTHTOKEN = "Bearer testAccessToken";
    @Mock
    @Qualifier("missionServiceImpl")
    MissionService missionService;
    CommonMission commonMission;

    Mission mission_common;
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @InjectMocks
    private CommonMissionController target;

    @BeforeEach
    void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        objectMapper = new ObjectMapper();

        mission_common = missionFixture1_Common();
        commonMission = commonMissionFixture1();
        ReflectionTestUtils.setField(commonMission, "mission", mission_common);
    }

    @DisplayName("공통미션 가져오기 - 성공")
    @Test
    void getCommonMission_success() throws Exception {
        //given
        final String url = "/api/v1/missions/common";
        when(missionService.getDailyCommonMission())
                .thenReturn(DailyCommonMissionResponse.of(commonMission));

        // when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get(url)
                        .header(HttpHeaders.AUTHORIZATION, AUTHTOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.mission", is(commonMission
                        .getMission().getContent())));

        verify(missionService, times(1)).getDailyCommonMission();
    }

    @DisplayName("해당 일자 공통미션 가져오기 - 실패 / 오늘의 공통미션 없음")
    @Test
    void getCommonMission_failure_daily_common_mission_not_found() throws Exception {
        //given
        final String url = "/api/v1/missions/common";
        when(missionService.getDailyCommonMission())
                .thenThrow(new MissionNotFoundException());

        // when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.get(url)
                        .header(HttpHeaders.AUTHORIZATION, AUTHTOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(MissionErrorCode.MISSION_NOT_FOUND.getStatus().value())))
                .andExpect(jsonPath("$.message", is(MissionErrorCode.MISSION_NOT_FOUND.getMessage())));
        verify(missionService, times(1)).getDailyCommonMission();
    }

}
