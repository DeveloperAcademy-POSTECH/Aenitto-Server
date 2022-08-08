package com.firefighter.aenitto.rooms.service;

import com.firefighter.aenitto.common.exception.room.*;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.ParticipateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.VerifyInvitationRequest;
import com.firefighter.aenitto.rooms.dto.response.VerifyInvitationResponse;
import com.firefighter.aenitto.rooms.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Qualifier(value = "roomServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    @Qualifier("roomRepositoryImpl")
    private final RoomRepository roomRepository;

    @Qualifier("memberRepositoryImpl")
    private final MemberRepository memberRepository;


    @Override
    @Transactional
    public Long createRoom(Member member, CreateRoomRequest createRoomRequest) {
        // Dto -> Entity
        final Room room = createRoomRequest.toEntity();

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

        memberRepository.updateMember(member);
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
    public Long participateRoom(Member member, Long roomId, ParticipateRoomRequest request) {
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
        memberRepository.updateMember(member);

        return roomId;
    }

    private void throwExceptionIfParticipating(UUID memberId, Long roomId) {
        try {
            roomRepository.findMemberRoomById(memberId, roomId);
            throw new RoomAlreadyParticipatingException();
        } catch (EmptyResultDataAccessException e) {}
    }
}
