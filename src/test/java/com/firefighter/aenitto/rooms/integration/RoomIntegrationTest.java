package com.firefighter.aenitto.rooms.integration;


import com.firefighter.aenitto.common.exception.mission.MissionErrorCode;
import com.firefighter.aenitto.common.exception.mission.MissionNotFoundException;
import com.firefighter.aenitto.common.exception.room.RoomErrorCode;
import com.firefighter.aenitto.common.exception.room.RoomNotParticipatingException;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.dto.RoomRequestDtoBuilder;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.ParticipateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.VerifyInvitationRequest;
import com.firefighter.aenitto.support.IntegrationTest;
import com.firefighter.aenitto.support.security.WithMockCustomMember;
import org.apache.coyote.Request;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WithMockCustomMember
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
                .andExpect(jsonPath("$.participatingRooms[0].participatingCount", is(1)))
                .andExpect(jsonPath("$.participatingRooms[0].capacity", is(13)));
    }

    @Sql({
            "classpath:member.sql",
            "classpath:room-pre.sql"
    })
    @DisplayName("방 정보 조회 (PRE) - 실패 (참여 중x)")
    @Test
    void getRoomDetail_fail_not_participating() throws Exception {
        // given
        final Long roomId = 2L;
        final String url = "/api/v1/rooms/{roomId}";

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get(url, roomId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is(RoomErrorCode.ROOM_NOT_PARTICIPATING.getMessage())));
    }

    @Sql({
            "classpath:member.sql",
            "classpath:room-processing.sql",
            "classpath:memberRoom.sql",
    })
    @DisplayName("방 정보 조회 (PROCESSING) - 실패 (Relation x)")
    @Test
    void getRoomDetail_fail_no_relation() throws Exception {
        // given
        final Long roomId = 2L;
        final String url = "/api/v1/rooms/{roomId}";

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get(url, roomId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(RoomErrorCode.RELATION_NOT_FOUND.getMessage())));
    }

    @Sql({
            "classpath:member.sql",
            "classpath:room-processing.sql",
            "classpath:memberRoom.sql",
            "classpath:relation.sql"
    })
    @DisplayName("방 정보 조회 (PROCESSING) - 실패 (금일의 개별 미션 설정 x)")
    @Test
    void getRoomDetail_fail_no_mission() throws Exception {
        // given
        final Long roomId = 2L;
        final String url = "/api/v1/rooms/{roomId}";

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get(url, roomId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(MissionErrorCode.MISSION_NOT_FOUND.getMessage())));
    }


    @Sql({
            "classpath:member.sql",
            "classpath:room-pre.sql",
            "classpath:memberRoom.sql",
    })
    @DisplayName("방 정보 조회 (PRE) - 성공")
    @Test
    void getRoomDetail_PRE_success() throws Exception {
        // given
        final Long roomId = 2L;
        final String url = "/api/v1/rooms/{roomId}";

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get(url, roomId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.room.id").exists())
                .andExpect(jsonPath("$.room.title").exists())
                .andExpect(jsonPath("$.room.startDate").exists())
                .andExpect(jsonPath("$.room.endDate").exists())
                .andExpect(jsonPath("$.room.state", is("PRE")))
                .andExpect(jsonPath("$.participants").exists())
                .andExpect(jsonPath("$.admin").exists())
                .andExpect(jsonPath("$.didViewRoulette").doesNotExist())
                .andExpect(jsonPath("$.manittee.nickname").doesNotExist())
                .andExpect(jsonPath("$.mission.id").doesNotExist())
                .andExpect(jsonPath("$.mission.content").doesNotExist())
                .andExpect(jsonPath("$.messages.count").doesNotExist());

    }

    // TODO: path enum (22.08.17)
    @Sql({
            "classpath:member.sql",
            "classpath:room-processing.sql",
            "classpath:memberRoom.sql",
            "classpath:message.sql",
            "classpath:relation.sql",
            "classpath:mission.sql"
    })
    @DisplayName("방 정보 조회 (PROCESSING) - 성공")
    @Test
    void getRoomDetail_PROCESSING_success() throws Exception {
        // given
        final Long roomId = 2L;
        final String url = "/api/v1/rooms/{roomId}";

        // when
        MemberRoom findMemberRoom1 = em.find(MemberRoom.class, 1L);
        assertThat(findMemberRoom1.didViewManitto()).isFalse();
        flushAndClear();

        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get(url, roomId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.room.id").exists())
                .andExpect(jsonPath("$.room.title").exists())
                .andExpect(jsonPath("$.room.startDate").exists())
                .andExpect(jsonPath("$.room.endDate").exists())
                .andExpect(jsonPath("$.room.state", is("PROCESSING")))
                .andExpect(jsonPath("$.participants").doesNotExist())
                .andExpect(jsonPath("$.admin").exists())
                .andExpect(jsonPath("$.didViewRoulette").exists())
                .andExpect(jsonPath("$.manittee.nickname").exists())
                .andExpect(jsonPath("$.mission.id").exists())
                .andExpect(jsonPath("$.mission.content").exists())
                .andExpect(jsonPath("$.messages.count").exists());

        MemberRoom findMemberRoom2 = em.find(MemberRoom.class, 1L);
        assertThat(findMemberRoom2.didViewManitto()).isTrue();
    }

    @Sql({
            "classpath:member.sql",
            "classpath:room-post.sql",
            "classpath:memberRoom.sql",
            "classpath:message.sql",
            "classpath:relation.sql",
    })
    @DisplayName("방 정보 조회 (POST) - 성공")
    @Test
    void getRoomDetail_POST_success() throws Exception {
        // given
        final Long roomId = 2L;
        final String url = "/api/v1/rooms/{roomId}";

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get(url, roomId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.room.id").exists())
                .andExpect(jsonPath("$.room.title").exists())
                .andExpect(jsonPath("$.room.startDate").exists())
                .andExpect(jsonPath("$.room.endDate").exists())
                .andExpect(jsonPath("$.room.state", is("POST")))
                .andExpect(jsonPath("$.participants").doesNotExist())
                .andExpect(jsonPath("$.admin").exists())
                .andExpect(jsonPath("$.didViewRoulette").doesNotExist())
                .andExpect(jsonPath("$.manittee.nickname").exists())
                .andExpect(jsonPath("$.mission.id").doesNotExist())
                .andExpect(jsonPath("$.mission.content").doesNotExist())
                .andExpect(jsonPath("$.messages.count").exists());
    }

    @Sql("classpath:room-getParticipatingRooms.sql")
    @DisplayName("참여 중인 방 조회 - 성공")
    @Test
    void findParticipatingRooms_success() throws Exception {
        // given
        final String url = "/api/v1/rooms";

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participatingRooms[0].id", is(4)))
                .andExpect(jsonPath("$.participatingRooms[1].id", is(3)))
                .andExpect(jsonPath("$.participatingRooms[2].id", is(1)))
                .andExpect(jsonPath("$.participatingRooms[3].id", is(5)))
                .andExpect(jsonPath("$.participatingRooms[4].id", is(2)));
    }
}
