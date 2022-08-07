package com.firefighter.aenitto.rooms.dto.response;

import com.firefighter.aenitto.rooms.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ParticipatingRoomsResponse {
    private final List<ParticipatingRoom> participatingRooms;

    public static ParticipatingRoomsResponse of(List<Room> rooms) {
        return new ParticipatingRoomsResponse(rooms.stream().map(ParticipatingRoom::of).collect(Collectors.toList()));
    }

    @Getter
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    @Builder
    public static class ParticipatingRoom {
        private final Long id;
        private final String title;
        private final String state;
        private final int participatingCount;
        private final int capacity;
        private final String startDate;
        private final String endDate;

        public static ParticipatingRoom of(Room room) {
            return ParticipatingRoom.builder()
                    .id(room.getId())
                    .title(room.getTitle())
                    .state(room.getState().toString())
                    .participatingCount(room.getMemberRooms().size())
                    .capacity(room.getCapacity())
                    .startDate(room.getStartDateValue())
                    .endDate(room.getEndDateValue())
                    .build();
        }
    }
}
