package com.firefighter.aenitto.rooms.controller;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.ParticipateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.VerifyInvitationRequest;
import com.firefighter.aenitto.rooms.dto.response.GetRoomStateResponse;
import com.firefighter.aenitto.rooms.dto.response.VerifyInvitationResponse;
import com.firefighter.aenitto.rooms.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @Valid @RequestBody final CreateRoomRequest createRoomRequest
    ) {
        final Member member = Member.builder().nickname("mock").build();
        final Long roomId = roomService.createRoom(member, createRoomRequest);
        return ResponseEntity.created(URI.create("/api/v1/rooms/" + roomId)).build();
    }

    @PostMapping("/invitations/verification")
    public ResponseEntity verifyInvitation(
            @Valid @RequestBody final VerifyInvitationRequest request
    ) {
        final Member member = mockLoginMember();
        final VerifyInvitationResponse response = roomService.verifyInvitation(member, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rooms/{roomId}/participants")
    public ResponseEntity participateRoom(
            @PathVariable final Long roomId,
            @RequestBody final ParticipateRoomRequest request
    ) {
        final Member member = mockLoginMember();
        roomService.participateRoom(member, roomId, request);
        return ResponseEntity.created(URI.create("/api/v1/rooms/" + roomId)).build();
    }

    @GetMapping("/rooms/{roomId}/state")
    public ResponseEntity<GetRoomStateResponse> getRoomState(
            @PathVariable final Long roomId
    ) {
        final Member member = mockLoginMember();
        return ResponseEntity.ok(roomService.getRoomState(member, roomId));
    }

    private Member mockLoginMember() {
        return Member.builder()
                .nickname("Mock")
                .build();
    }
}
