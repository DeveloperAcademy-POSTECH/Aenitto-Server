package com.firefighter.aenitto.rooms.service;

import com.firefighter.aenitto.common.exception.member.MemberNotFoundException;
import com.firefighter.aenitto.common.exception.room.*;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.ParticipateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.VerifyInvitationRequest;
import com.firefighter.aenitto.rooms.dto.response.GetRoomStateResponse;
import com.firefighter.aenitto.rooms.dto.response.ParticipatingRoomsResponse;
import com.firefighter.aenitto.rooms.dto.response.VerifyInvitationResponse;
import com.firefighter.aenitto.rooms.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@Qualifier(value = "roomServiceImpl")
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    @Qualifier("roomRepositoryImpl")
    private final RoomRepository roomRepository;

    @Qualifier("memberRepositoryImpl")
    private final MemberRepository memberRepository;


    @Override
    @Transactional
    public Long createRoom(Member currentMember, CreateRoomRequest createRoomRequest) {
        // Dto -> Entity
        final Room room = createRoomRequest.toEntity();
        final Member member = memberRepository.findByMemberId(currentMember.getId())
                .orElseThrow(MemberNotFoundException::new);

        // Room invitation 생성 -> 존재하지 않는 random 코드 나올 때 까지.
        do {
            room.createInvitation();
            try {
                roomRepository.findByInvitation(room.getInvitation());
            } catch (EmptyResultDataAccessException e) {
                break;
            }
        } while (true);

        // admin MemberRoom 생성 및 persist
        MemberRoom memberRoom = MemberRoom.builder()
                .admin(true)
                .build();

        memberRoom.setMemberRoom(member, room);
//        memberRepository.updateMember(currentMember);
        return roomRepository.saveRoom(room).getId();
    }


    @Override
    public VerifyInvitationResponse verifyInvitation(Member member, VerifyInvitationRequest verifyInvitationRequest) {
        final String invitation = verifyInvitationRequest.getInvitationCode();
        Room findRoom;

        // 초대코드로 Room 조회 -> 결과가 없을 경우 throw
        try {
            findRoom = roomRepository.findByInvitation(invitation);
        } catch (EmptyResultDataAccessException e) {
            throw new InvitationNotFoundException();
        }

        // roomId와 memberId로 MemberRoom 조회 -> 결과가 있을 경우 throw
        throwExceptionIfParticipating(member.getId(), findRoom.getId());

        return VerifyInvitationResponse.from(findRoom);
    }

    @Override
    @Transactional
    public Long participateRoom(Member currentMember, Long roomId, ParticipateRoomRequest request) {
        Member member = memberRepository.findByMemberId(currentMember.getId())
                .orElseThrow(MemberNotFoundException::new);
        // roomId와 memberId로 MemberRoom 조회 -> 결과가 있을 경우 throw
        throwExceptionIfParticipating(member.getId(), roomId);

        // roomId로 방 조회 -> 없을 경우 throw
        Room findRoom;
        try {
            findRoom = roomRepository.findRoomById(roomId);
        } catch (EmptyResultDataAccessException e) {
            throw new RoomNotFoundException();
        }

        // 방의 수용인원이 초과했을 경우 -> throw
        if (findRoom.unAcceptable()) throw new RoomCapacityExceededException();

        MemberRoom memberRoom = request.toEntity();
        memberRoom.setMemberRoom(member, findRoom);
        System.out.println(memberRoom.getId());
        return roomId;
    }

    @Override
    public GetRoomStateResponse getRoomState(Member member, Long roomId) {
        MemberRoom memberRoom;
        // 참여 중인 방이 아닐 경우 -> throw
        try {
            memberRoom = roomRepository.findMemberRoomById(member.getId(), roomId);
        } catch (EmptyResultDataAccessException e) {
            throw new RoomNotParticipatingException();
        }

        return GetRoomStateResponse.of(memberRoom.getRoom());
    }

    @Override
    public ParticipatingRoomsResponse getParticipatingRooms(Member member, Long cursor, int limit) {
        List<Room> participatingRooms = roomRepository.findParticipatingRoomsByMemberIdWithCursor(member.getId(), cursor, limit);
        return ParticipatingRoomsResponse.of(participatingRooms);
    }

    private void throwExceptionIfParticipating(UUID memberId, Long roomId) {
        try {
            roomRepository.findMemberRoomById(memberId, roomId);
            throw new RoomAlreadyParticipatingException();
        } catch (EmptyResultDataAccessException e) {}
    }
}
