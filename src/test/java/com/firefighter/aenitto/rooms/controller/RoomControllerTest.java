package com.firefighter.aenitto.rooms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.dto.RoomRequestDtoBuilder;
import com.firefighter.aenitto.rooms.dto.RoomResponseDtoBuilder;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.ParticipateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.VerifyInvitationRequest;
import com.firefighter.aenitto.rooms.dto.response.VerifyInvitationResponse;
import com.firefighter.aenitto.rooms.service.RoomService;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.firefighter.aenitto.members.MemberFixture.MEMBER_1;
import static com.firefighter.aenitto.rooms.RoomFixture.ROOM_1;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;


@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class RoomControllerTest {
    @InjectMocks
    RoomController roomController;

    @Mock @Qualifier("roomServiceImpl")
    RoomService roomService;

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    // Fixture
    private Member member;
    private Room room;

    @BeforeEach
    void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(roomController)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        objectMapper = new ObjectMapper();
        room = ROOM_1;
        member = MEMBER_1;
    }

    @DisplayName("방 생성 -> 성공")
    @Test
    void createRoom() throws Exception {
        // Mock
        when(roomService.createRoom(any(Member.class), any(CreateRoomRequest.class))).thenReturn(1L);

        // given
        final String uri = "/api/v1/rooms";

        // when
        final ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.post(uri)
                        .content(objectMapper.writeValueAsString(RoomRequestDtoBuilder.createRoomRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform.andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andDo(document("post-create", // 5
                        requestFields( // 6
                                fieldWithPath("room.title").description("Post 제목"), // 7
                                fieldWithPath("room.capacity").description("Post 내용").optional(), // 8
                                fieldWithPath("room.startDate").description("시작일").optional(),
                                fieldWithPath("room.endDate").description("마지막일"),
                                fieldWithPath("member.colorIdx").description("참여자 색상 index")
                        )
                ));
    }

    @DisplayName("방 생성 -> 실패")
    @Test
    void createRoomFail() throws Exception {
        // given
        final String uri = "/api/v1/rooms";

        // when
        final ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.post(uri)
                        .content(objectMapper.writeValueAsString(
                                CreateRoomRequest.builder()
                                        .title("qqqqqqqqqqqqqqq")
                                        .capacity(10)
                                        .startDate("2022.06.20")
                                        .endDate("2022.06.30")
                                        .colorIdx(1)
                                        .build())
                        ).contentType(MediaType.APPLICATION_JSON)
        );

        final ResultActions perform1 = mockMvc.perform(
                MockMvcRequestBuilders.post(uri)
                        .content(objectMapper.writeValueAsString(
                                CreateRoomRequest.builder()
                                        .title("자자자")
                                        .capacity(16)
                                        .endDate("2022.06.30")
                                        .startDate("2022.06.30")
                                        .build())
                        ).contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform.andExpect(status().isBadRequest());
        perform1.andExpect(status().isBadRequest());
    }

    @DisplayName("초대코드 검증 - 성공")
    @Test
    void verifyInvitation_success() throws Exception {
        // given
        final String url = "/api/v1/invitations/verification";
        final VerifyInvitationResponse response = RoomResponseDtoBuilder.verifyInvitationResponse();
        when(roomService.verifyInvitation(any(Member.class), any(VerifyInvitationRequest.class)))
                .thenReturn(response);

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(
                                VerifyInvitationRequest.builder()
                                        .invitationCode("A1B2C3")
                                        .build())
                        ).contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity", is(room.getCapacity())))
                .andExpect(jsonPath("$.title", is(room.getTitle())))
                .andExpect(jsonPath("$.participatingCount", is(1)))
                .andDo(document("초대코드 검증", requestFields(
                        fieldWithPath("invitationCode").description("초대코드")
                )))
                .andDo(document("초대코드 검증", responseFields(
                        fieldWithPath("id").description("멤버 id"),
                        fieldWithPath("title").description("방 제목"),
                        fieldWithPath("capacity").description("수용 가능 인원"),
                        fieldWithPath("participatingCount").description("현재 참여 인원"),
                        fieldWithPath("startDate").description("시작 일자"),
                        fieldWithPath("endDate").description("종료 일자")
                )));
    }

    @DisplayName("초대코드 검증 - 실패 (초대코드가 6자가 아닌 경우)")
    @Test
    void veriyInvitation_fail() throws Exception {
        // given
        final String url = "/api/v1/invitations/verification";

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(
                                VerifyInvitationRequest.builder()
                                        .invitationCode("1231234")
                                        .build()
                        )).contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform.andExpect(status().isBadRequest());
    }

    @DisplayName("방 참여 - 성공")
    @Test
    void participateRoom_success() throws Exception {
        // given
        final Long roomId = 1L;
        final String url = "/api/v1/rooms/" + roomId + "/participants";
        final ParticipateRoomRequest request = ParticipateRoomRequest.builder().colorIdx(1).build();
        when(roomService.participateRoom(any(Member.class), anyLong(), any(ParticipateRoomRequest.class)))
                .thenReturn(roomId);

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/rooms/1"))
                .andDo(document("방 참여", requestFields(
                        fieldWithPath("colorIdx").description("참여 색상")
                )));
    }
}