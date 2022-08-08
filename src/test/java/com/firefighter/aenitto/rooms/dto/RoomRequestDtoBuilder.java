package com.firefighter.aenitto.rooms.dto;

import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.ParticipateRoomRequest;
import com.firefighter.aenitto.rooms.dto.request.VerifyInvitationRequest;

public class RoomRequestDtoBuilder {

    public static CreateRoomRequest createRoomRequest() {
        return CreateRoomRequest.builder()
                .title("title")
                .capacity(10)
                .startDate("2022.06.20")
                .endDate("2022.06.30")
                .colorIdx(2)
                .build();
    }

    public static VerifyInvitationRequest verifyInvitationRequest() {
        return VerifyInvitationRequest.builder()
                .invitationCode("5R2DV2")
                .build();
    }

    public static ParticipateRoomRequest participateRoomRequest() {
        return ParticipateRoomRequest.builder()
                .colorIdx(1)
                .build();
    }
}
