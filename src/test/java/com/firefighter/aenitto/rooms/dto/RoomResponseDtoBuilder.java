package com.firefighter.aenitto.rooms.dto;

import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.dto.response.GetRoomStateResponse;
import com.firefighter.aenitto.rooms.dto.response.ParticipatingRoomsResponse;
import com.firefighter.aenitto.rooms.dto.response.VerifyInvitationResponse;

import java.util.List;


public class RoomResponseDtoBuilder {

    public static VerifyInvitationResponse verifyInvitationResponse(Room room) {
        return VerifyInvitationResponse.from(room);
    }

    public static GetRoomStateResponse getRoomStateResponse(Room room) {
        return GetRoomStateResponse.of(room);
    }

    public static ParticipatingRoomsResponse participatingRoomsResponse(List<Room> rooms) {
        return ParticipatingRoomsResponse.of(rooms);
    }
}
