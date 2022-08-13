package com.firefighter.aenitto.rooms.controller;

import com.firefighter.aenitto.common.annotation.CurrentMember;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.ParticipateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.VerifyInvitationRequest;
import com.firefighter.aenitto.rooms.dto.response.GetRoomStateResponse;
import com.firefighter.aenitto.rooms.dto.response.ParticipatingRoomsResponse;
import com.firefighter.aenitto.rooms.dto.response.VerifyInvitationResponse;
import com.firefighter.aenitto.rooms.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RoomController {
    @Qualifier("roomServiceImpl")
    private final RoomService roomService;

    @PostMapping("/rooms")
    public ResponseEntity createRoom(
            @CurrentMember Member member,
            @Valid @RequestBody final CreateRoomRequest createRoomRequest
    ) {
        final Long roomId = roomService.createRoom(member, createRoomRequest);
        return ResponseEntity.created(URI.create("/api/v1/rooms/" + roomId)).build();
    }

    @PostMapping("/invitations/verification")
    public ResponseEntity verifyInvitation(
            @CurrentMember Member member,
            @Valid @RequestBody final VerifyInvitationRequest request
    ) {
        final VerifyInvitationResponse response = roomService.verifyInvitation(member, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rooms/{roomId}/participants")
    public ResponseEntity participateRoom(
            @CurrentMember Member member,
            @PathVariable final Long roomId,
            @RequestBody final ParticipateRoomRequest request
    ) {
        roomService.participateRoom(member, roomId, request);
        return ResponseEntity.created(URI.create("/api/v1/rooms/" + roomId)).build();
    }

    @GetMapping("/rooms/{roomId}/state")
    public ResponseEntity<GetRoomStateResponse> getRoomState(
            @CurrentMember Member currentMember,
            @PathVariable final Long roomId
    ) {
        return ResponseEntity.ok(roomService.getRoomState(currentMember, roomId));
    }

    // TODO: RoomAPI 메타데이터 Response Header 에 넣기 (22.08.07)
    @GetMapping("/rooms")
    public ResponseEntity<ParticipatingRoomsResponse> findParticipatingRooms(
            @CurrentMember Member member,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "3") int limit
    ) {
        // cursor 있으면, next 가 있어야 함.
        return ResponseEntity.ok(roomService.getParticipatingRooms(member, cursor, limit));
    }

    @PatchMapping("/rooms/{roomId}/state")
    public ResponseEntity startAenitto(
            @CurrentMember Member member,
            @PathVariable Long roomId
    ) {
        roomService.startAenitto(member, roomId);
        return ResponseEntity.noContent().build();
    }
}
