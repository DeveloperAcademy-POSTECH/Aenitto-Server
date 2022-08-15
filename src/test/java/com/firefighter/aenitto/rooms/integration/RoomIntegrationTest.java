package com.firefighter.aenitto.rooms.integration;


import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.dto.RoomRequestDtoBuilder;
import com.firefighter.aenitto.rooms.dto.RoomResponseDtoBuilder;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.ParticipateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.VerifyInvitationRequest;
import com.firefighter.aenitto.support.IntegrationTest;
import com.firefighter.aenitto.support.security.WithMockCustomMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WithMockCustomMember
@ActiveProfiles("testdb")
public class RoomIntegrationTest extends IntegrationTest {
    private Room room;

    @DisplayName("방 생성 -> 성공")
    @Test
    void create_room() throws Exception {
        // given
        CreateRoomRequest request = RoomRequestDtoBuilder.createRoomRequest();

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/rooms")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @DisplayName("초대코드 검증 -> 성공")
    @Test
    void verifyInvitation_success() throws Exception {
        // given
        VerifyInvitationRequest request = VerifyInvitationRequest.builder()
                .invitationCode("XYQOTE").build();

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/invitations/verification")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity", is(13)))
                .andExpect(jsonPath("$.title", is("제목")))
                .andExpect(jsonPath("$.participatingCount", is(0)));
    }

    @DisplayName("방 참여 -> 성공")
    @Test
    @WithMockCustomMember
    void participate_room_success() throws Exception {
        // given
        ParticipateRoomRequest request = ParticipateRoomRequest.builder()
                        .colorIdx(1).build();

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/rooms/1/participants")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/rooms/1"));
    }

    @Sql("classpath:room.sql")
    @DisplayName("방 상태 조회 -> 성공")
    @Test
    void getStateRoom_success() throws Exception {

        // when, then
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/rooms/1/state")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state", is("PRE")));
    }

    @Sql("classpath:room.sql")
    @DisplayName("참여중인 방 조회 -> 성공")
    @Test
    void participatingRoom_success() throws Exception {
        // given, when, then
        mockMvc.perform(
                        MockMvcRequestBuilders.get( "/api/v1/rooms?" + "limit=" + 1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participatingRooms[0].id", is(1)))
                .andExpect(jsonPath("$.participatingRooms[0].title", is("제목")))
                .andExpect(jsonPath("$.participatingRooms[0].state", is("PRE")))
                .andExpect(jsonPath("$.participatingRooms[0].participatingCount", is(3)))
                .andExpect(jsonPath("$.participatingRooms[0].capacity", is(13)));
    }

    @Sql("classpath:room.sql")
    @DisplayName("함께하는 친구들 조회 -> 성공")
    @Test
    void get_room_participants_success() throws Exception {
        // given, when, then
        mockMvc.perform(
                        MockMvcRequestBuilders.get( "/api/v1/rooms/{roomId}/participants", 1L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)))
                .andExpect(jsonPath("$.members[0].nickname", is("nickname")))
                .andExpect(jsonPath("$.members[0].colorIdx", is(1)));
    }
}
