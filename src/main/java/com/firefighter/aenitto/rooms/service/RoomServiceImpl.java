package com.firefighter.aenitto.rooms.service;

import com.firefighter.aenitto.common.exception.member.MemberNotFoundException;
import com.firefighter.aenitto.common.exception.mission.MissionEmptyException;
import com.firefighter.aenitto.common.exception.mission.MissionNotFoundException;
import com.firefighter.aenitto.common.exception.room.*;
import com.firefighter.aenitto.common.utils.RoomComparator;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import com.firefighter.aenitto.messages.repository.MessageRepository;
import com.firefighter.aenitto.missions.domain.IndividualMission;
import com.firefighter.aenitto.missions.repository.MissionRepository;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.ParticipateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.VerifyInvitationRequest;
import com.firefighter.aenitto.rooms.dto.response.GetRoomStateResponse;
import com.firefighter.aenitto.rooms.dto.response.ParticipatingRoomsResponse;
import com.firefighter.aenitto.rooms.dto.response.RoomDetailResponse;
import com.firefighter.aenitto.rooms.dto.response.VerifyInvitationResponse;
import com.firefighter.aenitto.rooms.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    @Qualifier("missionRepositoryImpl")
    private final MissionRepository missionRepository;
    @Qualifier("messageRepositoryImpl")
    private final MessageRepository messageRepository;



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
            if (roomRepository.findByInvitation(room.getInvitation()).isEmpty()) {
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

        // 초대코드로 Room 조회 -> 결과가 없을 경우 throw
        Room findRoom = roomRepository.findByInvitation(invitation)
                .orElseThrow(InvitationNotFoundException::new);

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
        Room findRoom = roomRepository.findRoomById(roomId)
                .orElseThrow(RoomNotFoundException::new);

        // 방의 수용인원이 초과했을 경우 -> throw
        if (findRoom.unAcceptable()) throw new RoomCapacityExceededException();

        MemberRoom memberRoom = request.toEntity();
        memberRoom.setMemberRoom(member, findRoom);
        return roomId;
    }

    @Override
    public GetRoomStateResponse getRoomState(Member currentMember, Long roomId) {
        Member member = memberRepository.findByMemberId(currentMember.getId())
                .orElseThrow(MemberNotFoundException::new);

        // 참여 중인 방이 아닐 경우 -> throw
        MemberRoom memberRoom = throwExceptionIfNotParticipating(member.getId(), roomId);
        return GetRoomStateResponse.of(memberRoom.getRoom());
    }

    @Override
    @Transactional
    public RoomDetailResponse getRoomDetail(Member member, Long roomId) {
        final MemberRoom memberRoom = throwExceptionIfNotParticipating(member.getId(), roomId);
        final Room room = memberRoom.getRoom();

        switch (room.getState()) {
            case PRE:
                return RoomDetailResponse.buildPreResponse(room, memberRoom);
            case PROCESSING: {
                // 마니띠, 룰렛 봤는지, admin 인지, 미션, 읽지 않은 메시지 수
                Relation relation = roomRepository.findRelationByManittoId(member.getId(), roomId)
                        .orElseThrow(RelationNotFoundException::new);
                IndividualMission individualMission = missionRepository.findIndividualMissionByDate(LocalDate.now(), memberRoom.getId())
                        .orElseThrow(MissionNotFoundException::new);
                int unreadMessageCount = messageRepository.findUnreadMessageCount(member.getId(), roomId);
                boolean didView = memberRoom.didViewManitto();
                if (!didView) {
                    memberRoom.setViewManito();
                }
                return RoomDetailResponse.buildProcessingResponse(
                        room,
                        relation,
                        memberRoom,
                        didView,
                        individualMission.getMission(),
                        unreadMessageCount
                );
            }
            case POST: {
                Relation relation = roomRepository.findRelationByManittoId(member.getId(), roomId)
                        .orElseThrow(RelationNotFoundException::new);
                int unreadMessageCount = messageRepository.findUnreadMessageCount(member.getId(), roomId);
                return RoomDetailResponse.buildPostResponse(
                        room,
                        relation,
                        memberRoom,
                        unreadMessageCount
                );
            }
            default:
                // RoomState 가 올바르지 않음 Exception 던짐.
                return null;
        }
    }

    @Override
    public ParticipatingRoomsResponse getParticipatingRooms(Member member, Long cursor, int limit) {
        List<Room> participatingRooms = roomRepository.findParticipatingRoomsByMemberIdWithCursor(member.getId(), cursor, limit);
        return ParticipatingRoomsResponse.of(participatingRooms);
    }

    @Override
    public ParticipatingRoomsResponse getParticipatingRooms(Member member) {
        return ParticipatingRoomsResponse.of(RoomComparator.sortRooms(roomRepository.findAllParticipatingRooms(member.getId())));
    }

    @Override
    @Transactional
    public void startAenitto(Member member, Long roomId) {
        // 참여 중인 방이 아닐 경우 -> throw Exception
        MemberRoom memberRoom = throwExceptionIfNotParticipating(member.getId(), roomId);
        // 방장이 아닌 경우 -> throw Exception
        throwExceptionIfNotAdmin(memberRoom);
        // 이미 시작한 방일 경우 -> throw Exception
        Room room = memberRoom.getRoom();
        if (room.getState() != RoomState.PRE) {
            throw new RoomAlreadyStartedException();
        }
        // 최소 수용인원 이하일 경우 -> throw Exception
        if (room.cannotStart()) {
            throw new RoomInsufficientParticipantsException();
        }

        // 참여인원에 대하여 Relation 생성
        Relation.createRelations(room.getMemberRooms(), room);

        // RoomState 수정
        room.setState(RoomState.PROCESSING);
    }

    @Override
    @Transactional
    public void deleteRoom(Member member, Long roomId) {
        MemberRoom memberRoom = throwExceptionIfNotParticipating(member.getId(), roomId);
        throwExceptionIfNotAdmin(memberRoom);

        memberRoom.getRoom().delete();
    }


    private void throwExceptionIfParticipating(UUID memberId, Long roomId) {
        roomRepository.findMemberRoomById(memberId, roomId)
                .ifPresent(memberRoom -> {
                    throw new RoomAlreadyParticipatingException();
                });
    }

    private MemberRoom throwExceptionIfNotParticipating(UUID memberId, Long roomId) {
        return roomRepository.findMemberRoomById(memberId, roomId)
                .orElseThrow(RoomNotParticipatingException::new);
    }

    private void throwExceptionIfNotAdmin(MemberRoom memberRoom) {
        if (!memberRoom.isAdmin()) {
            throw new RoomUnAuthorizedException();
        }
    }
}
