package com.firefighter.aenitto.rooms.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import com.firefighter.aenitto.common.annotation.CurrentMember;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.ParticipateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.UpdateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.VerifyInvitationRequest;
import com.firefighter.aenitto.rooms.dto.response.GetRoomStateResponse;
import com.firefighter.aenitto.rooms.dto.response.ParticipatingRoomsResponse;
import com.firefighter.aenitto.rooms.dto.response.RoomParticipantsResponse;
import com.firefighter.aenitto.rooms.dto.response.RoomDetailResponse;
import com.firefighter.aenitto.rooms.dto.response.VerifyInvitationResponse;
import com.firefighter.aenitto.rooms.service.RoomService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RoomController {
	@Qualifier("roomServiceImpl")
	private final RoomService roomService;

	@PostMapping("/rooms")
	public ResponseEntity createRoom(
		@CurrentMember final Member currentMember,
		@Valid @RequestBody final CreateRoomRequest createRoomRequest
	) {
		final Long roomId = roomService.createRoom(currentMember, createRoomRequest);
		return ResponseEntity.created(URI.create("/api/v1/rooms/" + roomId)).build();
	}

	@PostMapping("/invitations/verification")
	public ResponseEntity verifyInvitation(
		@CurrentMember final Member currentMember,
		@Valid @RequestBody final VerifyInvitationRequest request
	) {
		final VerifyInvitationResponse response = roomService.verifyInvitation(currentMember, request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/rooms/{roomId}/participants")
	public ResponseEntity participateRoom(
		@CurrentMember Member currentMember,
		@PathVariable final Long roomId,
		@RequestBody final ParticipateRoomRequest request
	) {
		roomService.participateRoom(currentMember, roomId, request);
		return ResponseEntity.created(URI.create("/api/v1/rooms/" + roomId)).build();
	}

	@GetMapping("/rooms/{roomId}/state")
	public ResponseEntity<GetRoomStateResponse> getRoomState(
		@CurrentMember final Member currentMember,
		@PathVariable final Long roomId
	) {
		return ResponseEntity.ok(roomService.getRoomState(currentMember, roomId));
	}

	@GetMapping("/rooms/{roomId}")
	public ResponseEntity<RoomDetailResponse> getRoomDetail(
		@CurrentMember final Member member,
		@PathVariable final Long roomId
	) {
		return ResponseEntity.ok(roomService.getRoomDetail(member, roomId));
	}

	@GetMapping("/rooms")
	public ResponseEntity<ParticipatingRoomsResponse> findParticipatingRooms(
		@CurrentMember final Member currentMember
		//            @RequestParam(required = false) Long cursor,
		//            @RequestParam(defaultValue = "3") int limit
	) {
		return ResponseEntity.ok(roomService.getParticipatingRooms(currentMember));
	}

	@PatchMapping("/rooms/{roomId}/state")
	public ResponseEntity startAenitto(
		@CurrentMember final Member member,
		@PathVariable final Long roomId
	) {
		RoomDetailResponse.RelationInfo result = roomService.startAenitto(member, roomId);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/rooms/{roomId}/participants")
	public ResponseEntity<RoomParticipantsResponse> getRoomParticipants(
		@CurrentMember Member currentMember,
		@PathVariable Long roomId
	) {
		RoomParticipantsResponse roomParticipantsResponse = roomService.getRoomParticipants(currentMember, roomId);
		return ResponseEntity.ok(roomParticipantsResponse);
	}

	@DeleteMapping("/rooms/{roomId}")
	public ResponseEntity deleteRoom(
		@CurrentMember final Member member,
		@PathVariable final Long roomId
	) {
		roomService.deleteRoom(member, roomId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/rooms/{roomId}")
	public ResponseEntity updateRoom(
		@CurrentMember final Member member,
		@PathVariable final Long roomId,
		@Valid @RequestBody UpdateRoomRequest request
	) {
		roomService.updateRoom(member, roomId, request);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/rooms/{roomId}/participants")
	public ResponseEntity exitRoom(
		@CurrentMember final Member member,
		@PathVariable final Long roomId
	) {
		roomService.exitRoom(member, roomId);
		return ResponseEntity.noContent().build();
	}
}
