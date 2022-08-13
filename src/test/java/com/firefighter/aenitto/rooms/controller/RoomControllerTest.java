package com.firefighter.aenitto.rooms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefighter.aenitto.common.exception.ErrorCode;
import com.firefighter.aenitto.common.exception.GlobalExceptionHandler;
import com.firefighter.aenitto.common.exception.room.RoomErrorCode;
import com.firefighter.aenitto.common.exception.room.RoomNotFoundException;
import com.firefighter.aenitto.common.exception.room.RoomNotParticipatingException;
import com.firefighter.aenitto.common.exception.room.RoomUnAuthorizedException;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;
import java.util.List;

import static com.firefighter.aenitto.members.MemberFixture.memberFixture;
import static com.firefighter.aenitto.rooms.RoomFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
//import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
//import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;


@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
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
    private Room room1;
    private Room room2;
    private MemberRoom memberRoom;

    @BeforeEach
    void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(roomController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        objectMapper = new ObjectMapper();
        room1 = roomFixture1();
        room2 = roomFixture2();
        member = memberFixture();
        memberRoom = memberRoomFixture1(member, room1);
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
        final VerifyInvitationResponse response = RoomResponseDtoBuilder.verifyInvitationResponse(room1);
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
                .andExpect(jsonPath("$.capacity", is(room1.getCapacity())))
                .andExpect(jsonPath("$.title", is(room1.getTitle())))
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

    @DisplayName("방 상태 조회 - 실패 (참여 x)")
    @Test
    void getStateRoom_fail_not_participating() throws Exception {
        final Long roomId = 1L;
        final String url = "/api/v1/rooms/" + roomId + "/state";

        when(roomService.getRoomState(any(Member.class), anyLong()))
                .thenThrow(new RoomNotParticipatingException());

        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        perform
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(handler().handlerType(RoomController.class))
                .andExpect(jsonPath("$.status", is(HttpStatus.FORBIDDEN.value())))
                .andExpect(jsonPath("$.message", is(RoomErrorCode.ROOM_NOT_PARTICIPATING.getMessage())));

        verify(roomService, times(1)).getRoomState(any(Member.class), anyLong());
    }

    @DisplayName("방 상태 조회 - 성공")
    @Test
    void getStateRoom_success() throws Exception {
        final Long roomId = 1L;
        final String url = "/api/v1/rooms/" + roomId + "/state";
        when(roomService.getRoomState(any(Member.class), anyLong()))
                .thenReturn(RoomResponseDtoBuilder.getRoomStateResponse(room1));

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        perform
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state", is(room1.getState().toString())))
                .andDo(document("방 상태 조회", responseFields(
                        fieldWithPath("state").description("방 상태")
                )));
        verify(roomService, times(1)).getRoomState(any(Member.class), anyLong());
    }

    @DisplayName("참여 중인 방 조회 - 성공")
    @Test
    void participatingRoom_success() throws Exception {
        final Long cursor = 0L;
        final int count = 3;

        List<Room> rooms = new ArrayList<>();
        rooms.add(room1);
        rooms.add(room2);

        final String url = "/api/v1/rooms?cursor=" + cursor + "&count=" + count;
        when(roomService.getParticipatingRooms(any(Member.class), anyLong(), anyInt()))
                .thenReturn(RoomResponseDtoBuilder.participatingRoomsResponse(rooms));

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        perform
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("참여 중인 방 조회", responseFields(
                        fieldWithPath("participatingRooms").description("참여 중인 방"),
                        fieldWithPath("participatingRooms[0].id").description("Room Id"),
                        fieldWithPath("participatingRooms[0].title").description("방 제목"),
                        fieldWithPath("participatingRooms[0].state").description("방 상태"),
                        fieldWithPath("participatingRooms[0].participatingCount").description("참여 인원"),
                        fieldWithPath("participatingRooms[0].capacity").description("수용 인원"),
                        fieldWithPath("participatingRooms[0].startDate").description("시작일"),
                        fieldWithPath("participatingRooms[0].endDate").description("종료일")
                )));

        verify(roomService, times(1)).getParticipatingRooms(any(Member.class), anyLong(), anyInt());
    }

    @DisplayName("게임 시작 - 실패 (참여 중인 방이 아님) ")
    @Test
    void startAenitto_fail_not_participating() throws Exception {
        // given
        final Long roomId = 1L;
        final String url = "/api/v1/rooms/" + roomId + "/state";

        // when
        doThrow(new RoomNotParticipatingException()).when(roomService).startAenitto(any(Member.class), anyLong());

        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.patch(url)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        perform
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(RoomErrorCode.ROOM_NOT_PARTICIPATING.getStatus().value())))
                .andExpect(jsonPath("$.message", is(RoomErrorCode.ROOM_NOT_PARTICIPATING.getMessage())));
    }

    @DisplayName("게임 시작 - 실패 (방장이 아님) ")
    @Test
    void startAenitto_fail_unauthorized() throws Exception {
        // given
        final Long roomId = 1L;
        final String url = "/api/v1/rooms/" + roomId + "/state";

        // when
        doThrow(new RoomUnAuthorizedException()).when(roomService).startAenitto(any(Member.class), anyLong());

        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.patch(url)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        perform
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(RoomErrorCode.ROOM_UNAUTHORIZED.getStatus().value())))
                .andExpect(jsonPath("$.message", is(RoomErrorCode.ROOM_UNAUTHORIZED.getMessage())));
    }

    @DisplayName("게임 시작 - 성공")
    @Test
    void startAenitto_success() throws Exception {
        // given
        final Long roomId = 1L;
        final String url = "/api/v1/rooms/{roomId}/state";

        // when
        doNothing().when(roomService).startAenitto(any(Member.class), anyLong());
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.patch(url, roomId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document(
                        "마니또 시작하기",
                        pathParameters(
                                parameterWithName("roomId").description("방 id")
                        )));

        verify(roomService, times(1)).startAenitto(any(Member.class), anyLong());
    }
}