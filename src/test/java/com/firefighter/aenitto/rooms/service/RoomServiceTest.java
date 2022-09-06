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
import com.firefighter.aenitto.missions.service.MissionServiceImpl;
import com.firefighter.aenitto.rooms.RoomFixture;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;
import com.firefighter.aenitto.rooms.dto.RoomRequestDtoBuilder;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.ParticipateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.UpdateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.VerifyInvitationRequest;
import com.firefighter.aenitto.rooms.dto.response.*;
import com.firefighter.aenitto.rooms.repository.MemberRoomRepository;
import com.firefighter.aenitto.rooms.repository.MemberRoomRepositoryImpl;
import com.firefighter.aenitto.rooms.repository.RoomRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.*;

import static com.firefighter.aenitto.auth.CurrentUserDetailFixture.CURRENT_USER_DETAILS;
import static com.firefighter.aenitto.members.MemberFixture.*;
import static com.firefighter.aenitto.rooms.RoomFixture.*;
import static com.firefighter.aenitto.missions.IndividualMissionFixture.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @InjectMocks
    private RoomServiceImpl target;
    @Mock
    private RoomRepositoryImpl roomRepository;
    @Mock
    private MemberRepositoryImpl memberRepository;
    @Mock
    private MissionRepositoryImpl missionRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private MissionServiceImpl missionService;

    @Mock @Qualifier("memberRoomRepositoryImpl")
    private MemberRoomRepository memberRoomRepository;

    // Fixtures
    Room room1;
    Room room2;
    Room room3;
    Room room4;
    Room room5;
    Member member;
    Member member1;
    Member member2;
    Member member3;
    Member member4;
    Member member5;
    MemberRoom memberRoom;
    MemberRoom memberRoom1;
    MemberRoom memberRoom2;
    MemberRoom memberRoom3;
    MemberRoom memberRoom4;
    MemberRoom memberRoom5;

    CurrentUserDetails currentUserDetails;

    Mission mission1;
    IndividualMission individualMission1;

    @BeforeEach
    void setup() {
        currentUserDetails = CURRENT_USER_DETAILS;

        room1 = roomFixture1();
        room2 = roomFixture2();
        room3 = roomFixture3();
        room4 = roomFixture4();
        room5 = roomFixture5();

        member = memberFixture();
        member1 = memberFixture();
        member2 = memberFixture2();
        member3 = memberFixture3();
        member4 = memberFixture4();
        member5 = memberFixture5();

//        memberRoom = memberRoomFixture1(member, room1);
        memberRoom1 = memberRoomFixture1(member, room3);
        memberRoom2 = memberRoomFixture2(member2, room3);
        memberRoom3 = memberRoomFixture3(member3, room3);

        mission1 = MissionFixture.missionFixture2_Individual();
        individualMission1 = individualMissionFixture1();
    }

    @DisplayName("방 생성 성공")
    @Test
    void createRoomTest() {
        // mock
        when(memberRepository.findByMemberId(any())).thenReturn(Optional.ofNullable(currentUserDetails.getMember()));
        when(roomRepository.findByInvitation(anyString()))
                .thenReturn(Optional.of(Room.builder().build()))
                .thenReturn(Optional.of(Room.builder().build()))
                .thenReturn(Optional.of(Room.builder().build()))
                .thenReturn(Optional.empty());
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
                .thenReturn(Optional.empty());

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
                .thenReturn(Optional.of(room1));
        when(roomRepository.findMemberRoomById(any(), anyLong()))
                .thenReturn(Optional.of(memberRoom));

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
                .thenReturn(Optional.of(room1));
        when(roomRepository.findMemberRoomById(eq(member1.getId()), anyLong()))
                .thenReturn(Optional.empty());

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
                .thenReturn(Optional.of(memberRoom));

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
                .thenReturn(Optional.empty());
        when(roomRepository.findRoomById(anyLong()))
                .thenReturn(Optional.empty());

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
                .thenReturn(Optional.empty());
        when(roomRepository.findRoomById(anyLong()))
                .thenReturn(Optional.of(Room.builder().capacity(0).build()));

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
                .thenReturn(Optional.empty());
        when(roomRepository.findRoomById(anyLong()))
                .thenReturn(Optional.of(room1));

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
                .thenReturn(Optional.empty());

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
                .thenReturn(Optional.of(memberRoom));

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
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(RoomNotParticipatingException.class)
                .isThrownBy(() -> {
                    target.getRoomDetail(member1, roomId);
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
                .thenReturn(Optional.of(memberRoom2));

        RoomDetailResponse roomDetail = target.getRoomDetail(member1, roomId);
        RoomDetailResponse.RoomDetail roomDetail1 = roomDetail.getRoom();

        // then
        assertThat(roomDetail.getAdmin()).isEqualTo(false);
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
                .thenReturn(Optional.of(memberRoom2));
        when(roomRepository.findRelationByManittoId(any(UUID.class), anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(RelationNotFoundException.class)
                .isThrownBy(() -> {
                    target.getRoomDetail(member1, roomId);
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
                .thenReturn(Optional.of(memberRoom2));
        when(roomRepository.findRelationByManittoId(any(UUID.class), anyLong()))
                .thenReturn(Optional.of(room1.getRelations().get(0)));
        when(missionRepository.findIndividualMissionByDate(any(LocalDate.class), anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(MissionNotFoundException.class)
                .isThrownBy(() -> {
                    target.getRoomDetail(member1, roomId);
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
                .thenReturn(Optional.of(memberRoom2));
        when(roomRepository.findRelationByManittoId(any(UUID.class), anyLong()))
                .thenReturn(Optional.of(room1.getRelations().get(0)));
        when(missionRepository.findIndividualMissionByDate(any(LocalDate.class), anyLong()))
                .thenReturn(Optional.of(individualMission1));
        when(messageRepository.findUnreadMessageCount(any(UUID.class), anyLong()))
                .thenReturn(3);

        RoomDetailResponse roomDetail = target.getRoomDetail(member1, roomId);
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
                .thenReturn(Optional.of(memberRoom2));
        when(roomRepository.findRelationByManittoId(any(UUID.class), anyLong()))
                .thenReturn(Optional.of(room1.getRelations().get(0)));
        when(messageRepository.findUnreadMessageCount(any(UUID.class), anyLong()))
                .thenReturn(3);

        RoomDetailResponse roomDetail = target.getRoomDetail(member1, roomId);
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
                .thenReturn(Optional.empty());

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
                .thenReturn(Optional.of(memberRoom));

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
                .thenReturn(Optional.of(memberRoom));

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
                .thenReturn(Optional.of(memberRoom));


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
        memberRoom1 = RoomFixture.memberRoomFixture1(member1, room1);
        memberRoom2 = RoomFixture.memberRoomFixture2(member2, room1);
        memberRoom3 = RoomFixture.memberRoomFixture3(member3, room1);
        memberRoom4 = RoomFixture.memberRoomFixture4(member4, room1);
        memberRoom5 = RoomFixture.memberRoomFixture5(member5, room1);
        ReflectionTestUtils.setField(memberRoom1, "admin", true);
        room1.setState(RoomState.PRE);

        // when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(Optional.of(memberRoom1));
        doNothing().when(missionService).setInitialIndividualMission(any(MemberRoom.class));
        target.startAenitto(member1, roomId);

        // then
        assertThat(room1.getRelations().size()).isEqualTo(5);
        assertThat(room1.getRelations().get(0).getManittee()).isNotNull();
        assertThat(room1.getRelations().get(1).getManittee()).isNotNull();
        assertThat(room1.getRelations().get(2).getManittee()).isNotNull();
        assertThat(room1.getRelations().get(3).getManittee()).isNotNull();
        assertThat(room1.getRelations().get(4).getManittee()).isNotNull();

        verify(missionService, times(5)).setInitialIndividualMission(any(MemberRoom.class));
    }


    @DisplayName("방 멤버 조회 - 실패(참여하지 않은 방)")
    @Test
    void find_roomParticipants_fail_not_participating() {
        //given
        final Long roomId = 1L;

        //when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenThrow(RoomNotParticipatingException.class);

        //then
        assertThatExceptionOfType(RoomNotParticipatingException.class)
                .isThrownBy(() -> {
                    target.getRoomParticipants(member, roomId);
                });
    }

    @DisplayName("방 멤버 조회 - 성공")
    @Test
    void find_roomParticipants_success() {
        //given
        final Long roomId = 1L;
        List<MemberRoom> memberRooms;

        //when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(Optional.ofNullable(memberRoom1));
        RoomParticipantsResponse roomParticipantsResponse = target.getRoomParticipants(member, room1.getId());

        //then
        assertThat(roomParticipantsResponse.getCount()).isEqualTo(3);
        assertThat(roomParticipantsResponse.getMembers().get(0).getNickname()).isNotNull();
        assertThat(roomParticipantsResponse.getMembers().get(1).getNickname()).isNotNull();
        assertThat(roomParticipantsResponse.getMembers().get(2).getNickname()).isNotNull();
    }

    @DisplayName("참여 중인 방 조회 - 성공")
    @Test
    void getParticipatingRooms_success() {
        memberRoom2 = memberRoomFixture2(member1, room2);
        memberRoom3 = memberRoomFixture3(member1, room3);
        memberRoom4 = memberRoomFixture4(member1, room4);
        memberRoom5 = memberRoomFixture5(member1, room5);

        when(roomRepository.findAllParticipatingRooms(any(UUID.class)))
                .thenReturn(Arrays.asList(room5, room4, room3, room2, room1));

        ParticipatingRoomsResponse participatingRooms = target.getParticipatingRooms(member1);
        List<ParticipatingRoomsResponse.ParticipatingRoom> participatingRooms1 = participatingRooms.getParticipatingRooms();

        // then
        assertThat(participatingRooms1).hasSize(5);
        assertThat(participatingRooms1.get(0).getId()).isEqualTo(4L);
        assertThat(participatingRooms1.get(1).getId()).isEqualTo(2L);
        assertThat(participatingRooms1.get(2).getId()).isEqualTo(1L);
        assertThat(participatingRooms1.get(3).getId()).isEqualTo(5L);
        assertThat(participatingRooms1.get(4).getId()).isEqualTo(3L);
    }

    @DisplayName("방 삭제 - 실패 (참여 중 x)")
    @Test
    void deleteRoom_fail_not_participating() {
        // when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(Optional.empty());

        // then
        assertThatExceptionOfType(RoomNotParticipatingException.class)
                .isThrownBy(() -> {
                    target.deleteRoom(member1, room1.getId());
                });
    }

    @DisplayName("방 삭제 - 실패 (방장 x)")
    @Test
    void deleteRoom_fail_unauthorized() {
        // when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(Optional.of(memberRoom));

        // then
        assertThatExceptionOfType(RoomUnAuthorizedException.class)
                .isThrownBy(() -> {
                    target.deleteRoom(member1, room1.getId());
                });
    }

    @DisplayName("방 삭제 - 성공")
    @Test
    void deleteRoom_success() {
        // given
        ReflectionTestUtils.setField(memberRoom, "admin", true);

        // when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(Optional.of(memberRoom));
        target.deleteRoom(member1, room1.getId());

        // then
        assertThat(room1.isDeleted()).isTrue();
    }

    @DisplayName("방 수정 - 실패 (방 참여 x)")
    @Test
    void updateRoom_fail_not_participating() {
        //given
        UpdateRoomRequest request = RoomRequestDtoBuilder.updateRoomRequest();

        // when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(Optional.empty());

        // then
        assertThatExceptionOfType(RoomNotParticipatingException.class)
                .isThrownBy(() -> {
                    target.updateRoom(member1, room1.getId(), request);
                });
    }

    @DisplayName("방 수정 - 실패 (방장 x)")
    @Test
    void updateRoom_fail_not_admin() {
        //given
        memberRoom2 = memberRoomFixture2(member2, room2);
        ReflectionTestUtils.setField(memberRoom2, "admin", false);
        UpdateRoomRequest request = RoomRequestDtoBuilder.updateRoomRequest();

        // when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(Optional.of(memberRoom2));

        // then
        assertThatExceptionOfType(RoomUnAuthorizedException.class)
                .isThrownBy(() -> {
                    target.updateRoom(member1, room1.getId(), request);
                });
    }

    @DisplayName("방 수정 - 성공")
    @Test
    void updateRoom_success() {
        //given
        memberRoom2 = memberRoomFixture2(member2, room2);
        ReflectionTestUtils.setField(memberRoom2, "admin", true);
        UpdateRoomRequest request = RoomRequestDtoBuilder.updateRoomRequest();

        // when
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(Optional.of(memberRoom2));
        target.updateRoom(member2, room2.getId(), request);

        // then
        assertThat(room2.getTitle()).isEqualTo(request.getTitle());
        assertThat(room2.getCapacity()).isEqualTo(request.getCapacity());
        assertThat(room2.getStartDateValue()).isEqualTo(request.getStartDate());
        assertThat(room2.getEndDateValue()).isEqualTo(request.getEndDate());
    }

    @DisplayName("끝난 방 State 변경 - 성공")
    @Test
    void endAenitto_success() {
        // given
        ReflectionTestUtils.setField(room1, "endDate", LocalDate.now().minusDays(3));
        ReflectionTestUtils.setField(room2, "endDate", LocalDate.now().minusDays(2));
        ReflectionTestUtils.setField(room3, "endDate", LocalDate.now().minusDays(1));
        ReflectionTestUtils.setField(room4, "endDate", LocalDate.now());
        ReflectionTestUtils.setField(room5, "endDate", LocalDate.now().plusDays(1));

        ReflectionTestUtils.setField(room1, "state", RoomState.PROCESSING);
        ReflectionTestUtils.setField(room2, "state", RoomState.PROCESSING);
        ReflectionTestUtils.setField(room3, "state", RoomState.PROCESSING);
        ReflectionTestUtils.setField(room4, "state", RoomState.PROCESSING);
        ReflectionTestUtils.setField(room5, "state", RoomState.PROCESSING);

        // when
        when(roomRepository.findAllRooms()).thenReturn(Arrays.asList(room1, room2, room3, room4, room5));
        target.endAenitto();

        // then
        assertThat(room1.getState()).isEqualTo(RoomState.POST);
        assertThat(room2.getState()).isEqualTo(RoomState.POST);
        assertThat(room3.getState()).isEqualTo(RoomState.POST);
        assertThat(room4.getState()).isEqualTo(RoomState.PROCESSING);
        assertThat(room5.getState()).isEqualTo(RoomState.PROCESSING);
    }

    @DisplayName("방 나가기 - 실패 (참여중인 방 x)")
    @Test
    void exitRoom_fail_not_participating() {
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(RoomNotParticipatingException.class)
                .isThrownBy(() -> {
                    target.exitRoom(member1, 1L);
                });

        verify(roomRepository, times(1)).findMemberRoomById(any(UUID.class), anyLong());
    }

    @DisplayName("방 나가기 - 실패 (방장임)")
    @Test
    void exitRoom_fail_admin() {
        ReflectionTestUtils.setField(memberRoom, "admin", true);
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(Optional.of(memberRoom));

        assertThatExceptionOfType(AdminCannotExitRoomException.class)
                .isThrownBy(() -> {
                    target.exitRoom(member1, room1.getId());
                });

        verify(roomRepository, times(1)).findMemberRoomById(any(UUID.class), anyLong());
    }

    @DisplayName("방 나가기 - 성공")
    @Test
    void exitRoom_success() {
        ReflectionTestUtils.setField(memberRoom, "admin", false);
        when(roomRepository.findMemberRoomById(any(UUID.class), anyLong()))
                .thenReturn(Optional.of(memberRoom));
        doNothing().when(memberRoomRepository).delete(any(MemberRoom.class));

        target.exitRoom(member1, room1.getId());

        verify(roomRepository, times(1)).findMemberRoomById(any(UUID.class), anyLong());
        verify(memberRoomRepository, times(1)).delete(any(MemberRoom.class));
    }
}
