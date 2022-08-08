package com.firefighter.aenitto.rooms.service;

import com.firefighter.aenitto.common.exception.room.*;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepositoryImpl;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.dto.RoomRequestDtoBuilder;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.ParticipateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.VerifyInvitationRequest;
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

import java.util.UUID;

import static com.firefighter.aenitto.members.MemberFixture.MEMBER_1;
import static com.firefighter.aenitto.rooms.RoomFixture.MEMBER_ROOM_1;
import static com.firefighter.aenitto.rooms.RoomFixture.ROOM_1;
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

    // Fixtures
    private Room room;
    private Member member;
    private MemberRoom memberRoom;

    @BeforeEach
    void setup() {
        room = ROOM_1;
        member = MEMBER_1;
        memberRoom = MEMBER_ROOM_1;
    }

    @DisplayName("방 생성 성공")
    @Test
    void createRoomTest() {
        // mock
        when(roomRepository.findByInvitation(anyString()))
                .thenThrow(EmptyResultDataAccessException.class)
                .thenThrow(EmptyResultDataAccessException.class)
                .thenThrow(EmptyResultDataAccessException.class)
                .thenReturn(Room.builder().build());
        when(roomRepository.saveRoom(any(Room.class)))
                .thenReturn(room);

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
        verify(memberRepository, times(1)).updateMember(any(Member.class));
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
                    target.verifyInvitation(member, request);
                });
        verify(roomRepository, times(1)).findByInvitation(anyString());
    }

    @DisplayName("초대코드 검증 - 실패 (이미 유저가 참여중인 방)")
    @Test
    void verifyInvitation_fail_participating() {
        // mock
        when(roomRepository.findByInvitation(anyString()))
                .thenReturn(room);
        when(roomRepository.findMemberRoomById(any(), anyLong()))
                .thenReturn(memberRoom);

        // then
        final VerifyInvitationRequest verifyInvitationRequest = RoomRequestDtoBuilder.verifyInvitationRequest();

        // when, then
        assertThatExceptionOfType(RoomAlreadyParticipatingException.class)
                .isThrownBy(() -> {
                    target.verifyInvitation(member, verifyInvitationRequest);
                });
        verify(roomRepository, times(1)).findByInvitation(anyString());
        verify(roomRepository, times(1)).findMemberRoomById(any(), anyLong());
    }

    @DisplayName("초대코드 검증 - 성공")
    @Test
    void verifyInvitation_success() {
        // mock
        when(roomRepository.findByInvitation(anyString()))
                .thenReturn(room);
        when(roomRepository.findMemberRoomById(eq(member.getId()), anyLong()))
                .thenThrow(EmptyResultDataAccessException.class);

        // given
        final VerifyInvitationRequest verifyInvitationRequest = RoomRequestDtoBuilder.verifyInvitationRequest();

        // when
        VerifyInvitationResponse response = target.verifyInvitation(member, verifyInvitationRequest);

        // then
        assertThat(response.getCapacity()).isEqualTo(room.getCapacity());
        assertThat(response.getId()).isEqualTo(room.getId());
        assertThat(response.getTitle()).isEqualTo(room.getTitle());
        verify(roomRepository, times(1)).findByInvitation(anyString());
    }

    @DisplayName("방 참여 - 실패 (이미 유저가 참여중인 방")
    @Test
    void participateRoom_fail_participating() {
        // mock
        when(roomRepository.findMemberRoomById(eq(member.getId()), anyLong()))
                .thenReturn(memberRoom);

        // given
        final ParticipateRoomRequest request = RoomRequestDtoBuilder.participateRoomRequest();

        // when, then
        assertThatExceptionOfType(RoomAlreadyParticipatingException.class)
                .isThrownBy(() -> {
                    target.participateRoom(member, room.getId(), request);
                });
        verify(roomRepository, times(1)).findMemberRoomById(any(UUID.class), anyLong());
    }

    @DisplayName("방 참여 - 실패 (방 존재하지 않음)")
    @Test
    void participateRoom_fail_no_room() {
        // mock
        when(roomRepository.findMemberRoomById(eq(member.getId()), anyLong()))
                .thenThrow(EmptyResultDataAccessException.class);
        when(roomRepository.findRoomById(anyLong()))
                .thenThrow(EmptyResultDataAccessException.class);

        // given
        final ParticipateRoomRequest request = RoomRequestDtoBuilder.participateRoomRequest();

        // when, then
        assertThatExceptionOfType(RoomNotFoundException.class)
                .isThrownBy(() -> {
                    target.participateRoom(member, room.getId(), request);
                });
        verify(roomRepository, times(1)).findMemberRoomById(any(UUID.class), anyLong());
        verify(roomRepository, times(1)).findRoomById(anyLong());
    }

    @DisplayName("방 참여 - 실패 (방 수용인원 초과)")
    @Test
    void participateRoom_fail_unacceptable() {
        // mock
        when(roomRepository.findMemberRoomById(eq(member.getId()), anyLong()))
                .thenThrow(EmptyResultDataAccessException.class);
        when(roomRepository.findRoomById(anyLong()))
                .thenReturn(Room.builder().capacity(0).build());

        // given
        final ParticipateRoomRequest request = RoomRequestDtoBuilder.participateRoomRequest();

        // when, then
        assertThatExceptionOfType(RoomCapacityExceededException.class)
                .isThrownBy(() -> {
                    target.participateRoom(member, room.getId(), request);
                });
        verify(roomRepository, times(1)).findMemberRoomById(any(UUID.class), anyLong());
        verify(roomRepository, times(1)).findRoomById(anyLong());
    }


    @DisplayName("방 참여 - 성공")
    @Test
    void participateRoom_success() {
        // mock
        when(roomRepository.findMemberRoomById(eq(member.getId()), anyLong()))
                .thenThrow(EmptyResultDataAccessException.class);
        when(roomRepository.findRoomById(anyLong()))
                .thenReturn(room);

        // given
        final ParticipateRoomRequest request = RoomRequestDtoBuilder.participateRoomRequest();

        // when
        Long roomId = target.participateRoom(member, room.getId(), request);

        // then
        assertThat(roomId).isEqualTo(room.getId());
        verify(roomRepository, times(1)).findMemberRoomById(any(UUID.class), anyLong());
        verify(roomRepository, times(1)).findRoomById(anyLong());
    }
}
