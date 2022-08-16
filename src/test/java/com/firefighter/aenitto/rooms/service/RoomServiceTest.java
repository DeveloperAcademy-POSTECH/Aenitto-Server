package com.firefighter.aenitto.rooms.service;

import com.firefighter.aenitto.auth.token.CurrentUserDetails;
import com.firefighter.aenitto.common.exception.mission.MissionNotFoundException;
import com.firefighter.aenitto.common.exception.room.*;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepositoryImpl;
import com.firefighter.aenitto.messages.repository.MessageRepository;
import com.firefighter.aenitto.missions.IndividualMissionFixture;
import com.firefighter.aenitto.missions.MissionFixture;
import com.firefighter.aenitto.missions.domain.IndividualMission;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.repository.MissionRepositoryImpl;
import com.firefighter.aenitto.rooms.RoomFixture;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;
import com.firefighter.aenitto.rooms.dto.RoomRequestDtoBuilder;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.ParticipateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.VerifyInvitationRequest;
import com.firefighter.aenitto.rooms.dto.response.GetRoomStateResponse;
import com.firefighter.aenitto.rooms.dto.response.ParticipatingRoomsResponse;
import com.firefighter.aenitto.rooms.dto.response.RoomDetailResponse;
import com.firefighter.aenitto.rooms.dto.response.VerifyInvitationResponse;
import com.firefighter.aenitto.rooms.repository.RoomRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.firefighter.aenitto.auth.CurrentUserDetailFixture.CURRENT_USER_DETAILS;
import static com.firefighter.aenitto.members.MemberFixture.*;
import static com.firefighter.aenitto.rooms.RoomFixture.*;
import static com.firefighter.aenitto.missions.IndividualMissionFixture.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @InjectMocks private RoomServiceImpl target;
    @Mock private RoomRepositoryImpl roomRepository;
    @Mock private MemberRepositoryImpl memberRepository;
    @Mock private MissionRepositoryImpl missionRepository;
    @Mock private MessageRepository messageRepository;

    // Fixtures
    private Room room1;
    private Room room2;
    private Member member1;
    private Member member2;
    private Member member3;
    private Member member4;
    private Member member5;
    private MemberRoom memberRoom;

    private CurrentUserDetails currentUserDetails;
    private MemberRoom memberRoom2;
    private MemberRoom memberRoom3;
    private MemberRoom memberRoom4;
    private MemberRoom memberRoom5;

    Mission mission1;
    IndividualMission individualMission1;

    @BeforeEach
    void setup() {
        room1 = roomFixture1();
        room2 = roomFixture2();

        member1 = memberFixture();
        member2 = memberFixture2();
        member3 = memberFixture3();
        member4 = memberFixture4();
        member5 = memberFixture5();

        memberRoom = memberRoomFixture1(member1, room1);

        mission1 = MissionFixture.missionFixture2_Individual();
        individualMission1 = individualMissionFixture1();

        currentUserDetails = CURRENT_USER_DETAILS;
    }

    @DisplayName("방 생성 성공")
    @Test
    void createRoomTest() {
        // mock
        when(memberRepository.findByMemberId(any())).thenReturn(Optional.ofNullable(currentUserDetails.getMember()));
        when(roomRepository.findByInvitation(anyString()))
                .thenThrow(EmptyResultDataAccessException.class)
                .thenThrow(EmptyResultDataAccessException.class)
                .thenThrow(EmptyResultDataAccessException.class)
                .thenReturn(Room.builder().build());
        when(roomRepository.saveRoom(any(Room.class)))
                .thenReturn(room1);

        // given
        CreateRoomRequest createRoomRequest = CreateRoomRequest.builder()
                .title("방제목")
                .capacity(10)
                .startDate("2022.06.22")
                .endDate("2022.07.23")
                .build();

        // when
        Long roomId = target.createRoom(Member.builder().build(), createRoomRequest);

        // then
        assertThat(roomId).isEqualTo(1L);
        verify(roomRepository, times(1)).saveRoom(any(Room.class));
    }

    @DisplayName("초대코드 검증 - 실패 (초대코드 존재하지 않음)")
    @Test
    void verifyInvitation_fail_invalid() {
        // mock
        when(roomRepository.findByInvitation(anyString()))
                .thenThrow(EmptyResultDataAccessException.class);

        // given
        final VerifyInvitationRequest request = RoomRequestDtoBuilder.verifyInvitationRequest();

        // when, then
        assertThatExceptionOfType(InvitationNotFoundException.class)
                .isThrownBy(() -> {
                    target.verifyInvitation(member1, request);
                });
        verify(roomRepository, times(1)).findByInvitation(anyString());
    }

    @DisplayName("초대코드 검증 - 실패 (이미 유저가 참여중인 방)")
    @Test
    void verifyInvitation_fail_participating() {
        // mock
        when(roomRepository.findByInvitation(anyString()))
                .thenReturn(room1);
        when(roomRepository.findMemberRoomById(any(), anyLong()))
                .thenReturn(memberRoom);

        // then
        final VerifyInvitationRequest verifyInvitationRequest = RoomRequestDtoBuilder.verifyInvitationRequest();

        // when, then
        assertThatExceptionOfType(RoomAlreadyParticipatingException.class)
                .isThrownBy(() -> {
                    target.verifyInvitation(member1, verifyInvitationRequest);
                });
        verify(roomRepository, times(1)).findByInvitation(anyString());
        verify(roomRepository, times(1)).findMemberRoomById(any(), anyLong());
    }

    @DisplayName("초대코드 검증 - 성공")
    @Test
    void verifyInvitation_success() {
        // mock
        when(roomRepository.findByInvitation(anyString()))
                .thenReturn(room1);
        when(roomRepository.findMemberRoomById(eq(member1.getId()), anyLong()))
                .thenThrow(EmptyResultDataAccessException.class);

        // given
        final VerifyInvitationRequest verifyInvitationRequest = RoomRequestDtoBuilder.verifyInvitationRequest();

        // when
        VerifyInvitationResponse response = target.verifyInvitation(member1, verifyInvitationRequest);

        // then
        assertThat(response.getCapacity()).isEqualTo(room1.getCapacity());
        assertThat(response.getId()).isEqualTo(room1.getId());
        assertThat(response.getTitle()).isEqualTo(room1.getTitle());
        verify(roomRepository, times(1)).findByInvitation(anyString());
    }

    @DisplayName("방 참여 - 실패 (이미 유저가 참여중인 방")
    @Test
    void participateRoom_fail_participating() {
        // mock
        when(memberRepository.findByMemberId(any())).thenReturn(Optional.ofNullable(currentUserDetails.getMember()));
        when(roomRepository.findMemberRoomById(eq(currentUserDetails.getMember().getId()), anyLong()))
                .thenReturn(memberRoom);

        // given
        final ParticipateRoomRequest request = RoomRequestDtoBuilder.participateRoomRequest();

        // when, then
        assertThatExceptionOfType(RoomAlreadyParticipatingException.class)
                .isThrownBy(() -> {
                    target.participateRoom(member1, room1.getId(), request);
                });
        verify(roomRepository, times(1)).findMemberRoomById(any(UUID.class), anyLong());
    }

    @DisplayName("방 참여 - 실패 (방 존재하지 않음)")
    @Test
    void participateRoom_fail_no_room() {
        // mock
        when(memberRepository.findByMemberId(any())).thenReturn(Optional.ofNullable(currentUserDetails.getMember()));
        when(roomRepository.findMemberRoomById(eq(currentUserDetails.getMember().getId()), anyLong()))
                .thenThrow(EmptyResultDataAccessException.class);
        when(roomRepository.findRoomById(anyLong()))
                .thenThrow(EmptyResultDataAccessException.class);

        // given
        final ParticipateRoomRequest request = RoomRequestDtoBuilder.participateRoomRequest();

        // when, then
        assertThatExceptionOfType(RoomNotFoundException.class)
                .isThrownBy(() -> {
                    target.participateRoom(member1, room1.getId(), request);
                });
        verify(roomRepository, times(1)).findMemberRoomById(any(UUID.class), anyLong());
        verify(roomRepository, times(1)).findRoomById(anyLong());
    }

    @DisplayName("방 참여 - 실패 (방 수용인원 초과)")
    @Test
    void participateRoom_fail_unacceptable() {
        // mock
        when(memberRepository.findByMemberId(any())).thenReturn(Optional.ofNullable(currentUserDetails.getMember()));
        when(roomRepository.findMemberRoomById(eq(currentUserDetails.getMember().getId()), anyLong()))
                .thenThrow(EmptyResultDataAccessException.class);
        when(roomRepository.findRoomById(anyLong()))
                .thenReturn(Room.builder().capacity(0).build());

        // given
        final ParticipateRoomRequest request = RoomRequestDtoBuilder.participateRoomRequest();

        // when, then
        assertThatExceptionOfType(RoomCapacityExceededException.class)
                .isThrownBy(() -> {
                    target.participateRoom(member1, room1.getId(), request);
                });
        verify(roomRepository, times(1)).findMemberRoomById(any(UUID.class), anyLong());
        verify(roomRepository, times(1)).findRoomById(anyLong());
    }


    @DisplayName("방 참여 - 성공")
    @Test
    void participateRoom_success() {
        // mock
        when(memberRepository.findByMemberId(any())).thenReturn(Optional.ofNullable(currentUserDetails.getMember()));
        when(roomRepository.findMemberRoomById(eq(currentUserDetails.getMember().getId()), anyLong()))
                .thenThrow(EmptyResultDataAccessException.class);
        when(roomRepository.findRoomById(anyLong()))
                .thenReturn(room1);

        // given
        final ParticipateRoomRequest request = RoomRequestDtoBuilder.participateRoomRequest();

        // when
        Long roomId = target.participateRoom(member1, room1.getId(), request);

        // then
        assertThat(roomId).isEqualTo(room1.getId());
        verify(roomRepository, times(1)).findMemberRoomById(any(UUID.class), anyLong());
        verify(roomRepository, times(1)).findRoomById(anyLong());
    }

    @DisplayName("방 상태 확인 - 실패 (참여 중인 방 x)")
    @Test
    void getRoomstate_fail_not_participating() {
        //given
        when(memberRepository.findByMemberId(any())).thenReturn(Optional.ofNullable(currentUserDetails.getMember()));
        when(roomRepository.findMemberRoomById(eq(currentUserDetails.getMember().getId()), anyLong()))
                .thenThrow(EmptyResultDataAccessException.class);

        assertThatExceptionOfType(RoomNotParticipatingException.class)
                .isThrownBy(() -> {
                    target.getRoomState(currentUserDetails.getMember(), room1.getId());
                });
    }

    @DisplayName("방 상태 확인 - 성공")
    @Test
    void getRoomState_success() {
        // given
        when(memberRepository.findByMemberId(any())).thenReturn(Optional.ofNullable(currentUserDetails.getMember()));
        when(roomRepository.findMemberRoomById(eq(currentUserDetails.getMember().getId()), anyLong()))
                .thenReturn(memberRoom);

        // when
        GetRoomStateResponse roomState = target.getRoomState(currentUserDetails.getMember(), room1.getId());


        // then
        assertThat(roomState.getState()).isEqualTo("PROCESSING");
    }

    @DisplayName("참여 중인 방 조회 - 성공 (cursor 존재)")
    @Test
    void findParticipatingRoom_success_with_cursor() {
        // given
        List<Room> roomList = new ArrayList<>();
        roomList.add(room1);
        roomList.add(room2);

        when(roomRepository.findParticipatingRoomsByMemberIdWithCursor(any(UUID.class), anyLong(), anyInt()))
                .thenReturn(roomList);

        // when
        ParticipatingRoomsResponse participatingRooms = target.getParticipatingRooms(member1, 1L, 3);

        // then
        assertThat(participatingRooms.getParticipatingRooms().size()).isEqualTo(2);
        assertThat(participatingRooms.getParticipatingRooms().get(0).getState()).isEqualTo("PROCESSING");
    }

    @DisplayName("방 상세 정보 조회 (PRE) - 실패 (참여 중인 방이 아님)")
    @Test
    void getRoomDetail_PRE_fail_roomNo() {
        // given
        final Long roomId = 1L;
        final RoomState state = RoomState.PRE;

        // when, then
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenThrow(EmptyResultDataAccessException.class);

        assertThatExceptionOfType(RoomNotParticipatingException.class)
                .isThrownBy(() -> {
                    target.getRoomDetail(member1, roomId, state);
                });
    }

    @DisplayName("방 상세 정보 조회 (PRE) - 성공")
    @Test
    void getRoomDetail_PRE_success() {
        // given
        final Long roomId = 1L;
        final RoomState state = RoomState.PRE;
        memberRoom2 = memberRoomFixture2(member1, room2);

        // when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(memberRoom2);

        RoomDetailResponse roomDetail = target.getRoomDetail(member1, roomId, state);
        RoomDetailResponse.RoomDetail roomDetail1 = roomDetail.getRoom();

        // then
        assertThat(roomDetail.getAdmin()).isNull();
        assertThat(roomDetail.getDidViewRoulette()).isNull();
        assertThat(roomDetail.getManittee()).isNull();
        assertThat(roomDetail.getMessages()).isNull();
        assertThat(roomDetail1.getId()).isEqualTo(2L);
        assertThat(roomDetail1.getState()).isEqualTo("PRE");
        assertThat(roomDetail1.getTitle()).isEqualTo("방제목2");
    }

    @DisplayName("방 상세 정보 조회 (PROCESSING) - 실패 (마니또-마니띠 관계 x)")
    @Test
    void getRoomDetail_PROCESSING_fail_no_matching_relation() {
        // given
        final Long roomId = 1L;
        final RoomState state = RoomState.PROCESSING;
        memberRoom2 = memberRoomFixture2(member1, room1);

        // when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(memberRoom2);
        when(roomRepository.findRelationByManittoId(any(UUID.class), anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(RelationNotFoundException.class)
                .isThrownBy(() -> {
                    target.getRoomDetail(member1, roomId, state);
                });
    }

    @DisplayName("방 상세 정보 조회 (PROCESSING) - 실패 (개별 미션 x)")
    @Test
    void getRoomDetail_PROCESSING_fail_no_individualmission() {
        final Long roomId = 1L;
        final RoomState state = RoomState.PROCESSING;
        memberRoom2 = memberRoomFixture2(member2, room1);
        Relation.createRelations(room1.getMemberRooms(), room1);

        // when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(memberRoom2);
        when(roomRepository.findRelationByManittoId(any(UUID.class), anyLong()))
                .thenReturn(Optional.of(room1.getRelations().get(0)));
        when(missionRepository.findIndividualMissionByDate(any(LocalDate.class), anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(MissionNotFoundException.class)
                .isThrownBy(() -> {
                    target.getRoomDetail(member1, roomId, state);
                });
    }

    @DisplayName("방 상세 정보 조회 (PROCESSING) - 성공")
    @Test
    void getRoomDetail_PROCESSING_success() {
        // given
        final Long roomId = 1L;
        final RoomState state = RoomState.PROCESSING;
        memberRoom2 = memberRoomFixture2(member2, room1);
        Relation.createRelations(room1.getMemberRooms(), room1);
        ReflectionTestUtils.setField(individualMission1, "mission", mission1);

        // when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(memberRoom2);
        when(roomRepository.findRelationByManittoId(any(UUID.class), anyLong()))
                .thenReturn(Optional.of(room1.getRelations().get(0)));
        when(missionRepository.findIndividualMissionByDate(any(LocalDate.class), anyLong()))
                .thenReturn(Optional.of(individualMission1));
        when(messageRepository.findUnreadMessageCount(any(UUID.class), anyLong()))
                .thenReturn(3);

        RoomDetailResponse roomDetail = target.getRoomDetail(member1, roomId, state);
        RoomDetailResponse.RoomDetail room = roomDetail.getRoom();
        RoomDetailResponse.ManitteeInfo manittee = roomDetail.getManittee();
        RoomDetailResponse.MessageInfo messages = roomDetail.getMessages();
        RoomDetailResponse.MissionInfo mission = roomDetail.getMission();

        // then
        assertThat(room.getTitle()).isEqualTo(room1.getTitle());
        assertThat(room.getState()).isEqualTo("PROCESSING");
        assertThat(room.getId()).isEqualTo(room1.getId());
        assertThat(manittee.getNickname()).isNotNull();
        assertThat(messages.getCount()).isEqualTo(3);
        assertThat(mission.getContent()).isEqualTo(individualMission1.getMission().getContent());
    }

    @DisplayName("방 상세 정보 조회 (POST) - 성공")
    @Test
    void getRoomDetail_POST_success() {
        // given
        final Long roomId = 1L;
        final RoomState state = RoomState.POST;
        memberRoom2 = memberRoomFixture2(member2, room1);
        Relation.createRelations(room1.getMemberRooms(), room1);
        room1.setState(state);
        ReflectionTestUtils.setField(individualMission1, "mission", mission1);

        // when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(memberRoom2);
        when(roomRepository.findRelationByManittoId(any(UUID.class), anyLong()))
                .thenReturn(Optional.of(room1.getRelations().get(0)));
        when(messageRepository.findUnreadMessageCount(any(UUID.class), anyLong()))
                .thenReturn(3);

        RoomDetailResponse roomDetail = target.getRoomDetail(member1, roomId, state);
        RoomDetailResponse.RoomDetail room = roomDetail.getRoom();
        RoomDetailResponse.ManitteeInfo manittee = roomDetail.getManittee();
        RoomDetailResponse.MessageInfo messages = roomDetail.getMessages();

        // then
        assertThat(room.getTitle()).isEqualTo(room1.getTitle());
        assertThat(room.getState()).isEqualTo("POST");
        assertThat(room.getId()).isEqualTo(room1.getId());
        assertThat(manittee.getNickname()).isNotNull();
        assertThat(messages.getCount()).isEqualTo(3);
        assertThat(roomDetail.getMission()).isNull();
    }


    @DisplayName("게임 시작 - 실패 (참여 중인 방이 아님)")
    @Test
    void startAenitto_fail_not_participating() {
        // given
        final Long roomId = 1L;

        // when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenThrow(EmptyResultDataAccessException.class);

        // then
        assertThatExceptionOfType(RoomNotParticipatingException.class)
                .isThrownBy(() -> {
                    target.startAenitto(member1, roomId);
                });


    }

    @DisplayName("게임 시작 - 실패 (방장이 아님)")
    @Test
    void startAenitto_fail_unauthorized() {
        // given
        final Long roomId = 1L;
        ReflectionTestUtils.setField(memberRoom, "admin", false);


        // when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(memberRoom);

        // then
        assertThatExceptionOfType(RoomUnAuthorizedException.class)
                .isThrownBy(() -> {
                    target.startAenitto(member1, roomId);
                });
    }

    @DisplayName("게임 시작 - 실패 (이미 시작한 방)")
    @Test
    void startAenitto_fail_already_started() {
        // given
        final Long roomId = 1L;
        ReflectionTestUtils.setField(memberRoom, "admin", true);
        room1.setState(RoomState.PROCESSING);

        // when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(memberRoom);

        // then
        assertThatExceptionOfType(RoomAlreadyStartedException.class)
                .isThrownBy(() -> {
                    target.startAenitto(member1, roomId);
                });
    }

    @DisplayName("게임 시작 - 실패 (최소 수용인원 이하)")
    @Test
    void startAenitto_fail_lack_participants() {
        // given
        final Long roomId = 1L;
        ReflectionTestUtils.setField(memberRoom, "admin", true);
        room1.setState(RoomState.PRE);

        // when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(memberRoom);


        // then
        assertThatExceptionOfType(RoomInsufficientParticipantsException.class)
                .isThrownBy(() -> {
                    target.startAenitto(member1, roomId);
                });
    }

    @DisplayName("게임 시작 - 성공")
    @Test
    void startAenitto_success() {
        // given
        final Long roomId = 1L;
        ReflectionTestUtils.setField(memberRoom, "admin", true);
        room1.setState(RoomState.PRE);
        memberRoom2 = RoomFixture.memberRoomFixture2(member2, room1);
        memberRoom3 = RoomFixture.memberRoomFixture3(member3, room1);
        memberRoom4 = RoomFixture.memberRoomFixture4(member4, room1);
        memberRoom5 = RoomFixture.memberRoomFixture5(member5, room1);

        // when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(memberRoom);
        target.startAenitto(member1, roomId);

        // then
        assertThat(room1.getRelations().size()).isEqualTo(5);
        assertThat(room1.getRelations().get(0).getManittee()).isNotNull();
        assertThat(room1.getRelations().get(1).getManittee()).isNotNull();
        assertThat(room1.getRelations().get(2).getManittee()).isNotNull();
        assertThat(room1.getRelations().get(3).getManittee()).isNotNull();
        assertThat(room1.getRelations().get(4).getManittee()).isNotNull();
    }
}
