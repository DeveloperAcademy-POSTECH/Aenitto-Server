package com.firefighter.aenitto.rooms.dto.response;

import com.firefighter.aenitto.rooms.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class GetRoomStateResponse {
    private String state;

    public static GetRoomStateResponse of(Room room) {
        return new GetRoomStateResponse(room.getState().toString());
    }
}
