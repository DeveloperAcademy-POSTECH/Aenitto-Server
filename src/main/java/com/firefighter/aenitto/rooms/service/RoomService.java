package com.firefighter.aenitto.rooms.service;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.ParticipateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.UpdateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.VerifyInvitationRequest;
import com.firefighter.aenitto.rooms.dto.response.*;

public interface RoomService {
	public Long createRoom(Member member, CreateRoomRequest createRoomRequest);

	public VerifyInvitationResponse verifyInvitation(Member member, VerifyInvitationRequest verifyInvitationRequest);

	public Long participateRoom(Member member, Long roomId, ParticipateRoomRequest request);

	public GetRoomStateResponse getRoomState(Member member, Long roomId);

	public ParticipatingRoomsResponse getParticipatingRooms(Member member, Long cursor, int limit);

	public ParticipatingRoomsResponse getParticipatingRooms(Member member);

	public RoomDetailResponse getRoomDetail(Member member, Long roomId);

	public RoomDetailResponse.RelationInfo startAenitto(Member member, Long roomId);

	public RoomParticipantsResponse getRoomParticipants(Member currentMember, Long roomId);

	public void deleteRoom(Member member, Long roomId);

	public void updateRoom(Member member, Long roomId, UpdateRoomRequest request);

	public void exitRoom(Member member, Long roomId);

	public void endAenitto();
}
