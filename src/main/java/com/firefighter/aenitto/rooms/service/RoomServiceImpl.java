package com.firefighter.aenitto.rooms.service;

import com.firefighter.aenitto.common.exception.member.MemberNotFoundException;
import com.firefighter.aenitto.common.exception.mission.MissionNotFoundException;
import com.firefighter.aenitto.common.exception.room.AdminCannotExitRoomException;
import com.firefighter.aenitto.common.exception.room.InvitationNotFoundException;
import com.firefighter.aenitto.common.exception.room.RelationNotFoundException;
import com.firefighter.aenitto.common.exception.room.RoomAlreadyParticipatingException;
import com.firefighter.aenitto.common.exception.room.RoomAlreadyStartedException;
import com.firefighter.aenitto.common.exception.room.RoomCapacityExceededException;
import com.firefighter.aenitto.common.exception.room.RoomInsufficientParticipantsException;
import com.firefighter.aenitto.common.exception.room.RoomNotFoundException;
import com.firefighter.aenitto.common.exception.room.RoomNotParticipatingException;
import com.firefighter.aenitto.common.exception.room.RoomUnAuthorizedException;
import com.firefighter.aenitto.common.utils.RoomComparator;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import com.firefighter.aenitto.messages.repository.MessageRepository;
import com.firefighter.aenitto.missions.domain.IndividualMission;
import com.firefighter.aenitto.missions.repository.IndividualMissionRepository;
import com.firefighter.aenitto.missions.repository.MissionRepository;
import com.firefighter.aenitto.missions.service.MissionService;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.ParticipateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.UpdateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.VerifyInvitationRequest;
import com.firefighter.aenitto.rooms.dto.response.GetRoomStateResponse;
import com.firefighter.aenitto.rooms.dto.response.ParticipatingRoomsResponse;
import com.firefighter.aenitto.rooms.dto.response.RoomDetailResponse;
import com.firefighter.aenitto.rooms.dto.response.RoomParticipantsResponse;
import com.firefighter.aenitto.rooms.dto.response.VerifyInvitationResponse;
import com.firefighter.aenitto.rooms.repository.MemberRoomRepository;
import com.firefighter.aenitto.rooms.repository.RelationRepository;
import com.firefighter.aenitto.rooms.repository.RoomRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Qualifier(value = "roomServiceImpl")
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

  @Qualifier("roomRepositoryImpl")
  private final RoomRepository roomRepository;

  @Qualifier("relationRepositoryImpl")
  private final RelationRepository relationRepository;

  private final MemberRepository memberRepository;

  private final MissionRepository missionRepository;

  @Qualifier("messageRepositoryImpl")
  private final MessageRepository messageRepository;
  @Qualifier("missionServiceImpl")
  private final MissionService missionService;
  private final MemberRoomRepository memberRoomRepository;
  private final IndividualMissionRepository individualMissionRepository;

  @Override
  @Transactional
  public Long createRoom(Member currentMember, CreateRoomRequest createRoomRequest) {
    // Dto -> Entity
    final Room room = createRoomRequest.toEntity();
    final Member member = memberRepository.findById(currentMember.getId())
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
        .colorIdx(createRoomRequest.getMember().getColorIdx())
        .build();

    memberRoom.setMemberRoom(member, room);
    return roomRepository.saveRoom(room).getId();
  }

  @Override
  public VerifyInvitationResponse verifyInvitation(Member member,
      VerifyInvitationRequest verifyInvitationRequest) {
    final String invitation = verifyInvitationRequest.getInvitationCode();

    // 초대코드로 Room 조회 -> 결과가 없을 경우 throw
    Room findRoom = roomRepository.findByInvitation(invitation)
        .orElseThrow(InvitationNotFoundException::new);

    return VerifyInvitationResponse.from(findRoom);
  }

  @Override
  @Transactional
  public Long participateRoom(Member currentMember, Long roomId, ParticipateRoomRequest request) {
    Member member = memberRepository.findById(currentMember.getId())
        .orElseThrow(MemberNotFoundException::new);

    // roomId와 memberId로 MemberRoom 조회 -> 결과가 있을 경우 throw
    throwExceptionIfParticipating(member.getId(), roomId);

    // roomId로 방 조회 -> 없을 경우 throw
    Room findRoom = roomRepository.findRoomById(roomId)
        .orElseThrow(RoomNotFoundException::new);

    // 방의 수용인원이 초과했을 경우 -> throw
    if (findRoom.unAcceptable()) {
      throw new RoomCapacityExceededException();
    }

    if (findRoom.isNotPre()) {
      throw new RoomAlreadyStartedException();
    }

    MemberRoom memberRoom = request.toEntity();
    memberRoom.setMemberRoom(member, findRoom);

    return roomId;
  }

  @Override
  public GetRoomStateResponse getRoomState(Member currentMember, Long roomId) {
    Member member = memberRepository.findById(currentMember.getId())
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
        Relation relationManitto = roomRepository.findRelationByManittoId(member.getId(), roomId)
            .orElseThrow(RelationNotFoundException::new);
        Relation relationManittee = relationRepository.findByRoomIdAndManitteeId(roomId,
                member.getId())
            .orElseThrow(RelationNotFoundException::new);
        IndividualMission individualMission = individualMissionRepository.findIndividualMissionByDateAndRoomId(
                LocalDate.now(), roomId)
            .orElseThrow(MissionNotFoundException::new);
        int unreadMessageCount = messageRepository.findUnreadMessageCount(member.getId(), roomId);
        boolean didView = memberRoom.didViewManitto();
        if (!didView) {
          memberRoom.setViewManito();
        }
        return RoomDetailResponse.buildProcessingResponse(
            room,
            relationManitto,
            relationManittee,
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
    List<Room> participatingRooms = roomRepository.findParticipatingRoomsByMemberIdWithCursor(
        member.getId(),
        cursor, limit);
    return ParticipatingRoomsResponse.of(participatingRooms);
  }

  @Override
  public ParticipatingRoomsResponse getParticipatingRooms(Member member) {
    return ParticipatingRoomsResponse.of(
        RoomComparator.sortRooms(roomRepository.findAllParticipatingRooms(member.getId())));
  }

  @Override
  @Transactional
  public RoomDetailResponse.RelationInfo startAenitto(Member member, Long roomId) {
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
    Relation.createRelations(room);

    // 참여인원에 대하여 individual Mission 생성
    missionService.setInitialIndividualMission(room);

    // RoomState 수정
    room.setState(RoomState.PROCESSING);
    Relation adminManitteeRelation = roomRepository.findRelationByManittoId(member.getId(), roomId)
        .orElseThrow(RelationNotFoundException::new);
    return RoomDetailResponse.RelationInfo.ofManittee(adminManitteeRelation);
  }

  @Override
  public RoomParticipantsResponse getRoomParticipants(Member currentMember, Long roomId) {
    MemberRoom memberRoom = throwExceptionIfNotParticipating(currentMember.getId(), roomId);
    return RoomParticipantsResponse.of(memberRoom.getRoom().getMemberRooms());
  }

  @Override
  @Transactional
  public void deleteRoom(Member member, Long roomId) {
    MemberRoom memberRoom = throwExceptionIfNotParticipating(member.getId(), roomId);
    throwExceptionIfNotAdmin(memberRoom);

    memberRoom.getRoom().delete();
  }

  @Override
  @Transactional
  public void updateRoom(Member member, Long roomId, UpdateRoomRequest request) {
    MemberRoom memberRoom = throwExceptionIfNotParticipating(member.getId(), roomId);
    throwExceptionIfNotAdmin(memberRoom);

    memberRoom.getRoom().updateRoom(request);
  }

  @Override
  @Transactional
  public void exitRoom(Member member, Long roomId) {
    MemberRoom memberRoom = throwExceptionIfNotParticipating(member.getId(), roomId);
    throwExceptionIfAdmin(memberRoom);
    memberRoomRepository.delete(memberRoom);
  }

  @Override
  @Transactional
  public void endAenitto() {
    roomRepository.findAllRooms().stream()
        .filter(Room::isProcessingAndExpired)
        .forEach(room -> {
          room.setState(RoomState.POST);
        });
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

  private void throwExceptionIfAdmin(MemberRoom memberRoom) {
    if (memberRoom.isAdmin()) {
      throw new AdminCannotExitRoomException();
    }
  }
}
