package com.firefighter.aenitto.rooms.dto.response;

import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RoomDetailResponse {
    private final RoomDetail room;
    private final ManitteeInfo manittee;
    private final MissionInfo mission;
    private final Boolean didViewRoulette;
    private final Boolean admin;
    private final MessageInfo messages;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class RoomDetail {
        private final Long id;
        private final String title;
        private final String startDate;
        private final String endDate;
        private final String state;

        public static RoomDetail of (Room room) {
            return RoomDetail.builder()
                    .id(room.getId())
                    .title(room.getTitle())
                    .startDate(room.getStartDateValue())
                    .endDate(room.getEndDateValue())
                    .state(room.getState().toString())
                    .build();
        }
    }

    public static RoomDetailResponse buildPreResponse(Room room) {
        return RoomDetailResponse.builder()
                .room(RoomDetail.of(room))
                .build();
    }

    public static RoomDetailResponse buildProcessingResponse(Room room, MemberRoom memberRoom) {
        return RoomDetailResponse.builder()
                .room(RoomDetail.of(room))
                .didViewRoulette(memberRoom.didViewManitto())
                .build();
    }

    public static RoomDetailResponse buildPostResponse(Room room, MemberRoom memberRoom) {
        return RoomDetailResponse.builder()
                .room(RoomDetail.of(room))
                .build();
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class ManitteeInfo {
        private final String nickname;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class MissionInfo {
        private final Long id;
        private final String content;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class MessageInfo {
        private final int count;
    }
}
