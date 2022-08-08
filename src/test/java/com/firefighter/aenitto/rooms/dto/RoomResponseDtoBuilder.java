package com.firefighter.aenitto.rooms.dto;

import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.dto.response.VerifyInvitationResponse;

import static com.firefighter.aenitto.rooms.RoomFixture.ROOM_1;

public class RoomResponseDtoBuilder {
    private static final Room room = ROOM_1;

    public static VerifyInvitationResponse verifyInvitationResponse() {
        return VerifyInvitationResponse.from(room);
    }
}
