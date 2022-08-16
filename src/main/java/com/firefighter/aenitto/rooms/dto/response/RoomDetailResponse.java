package com.firefighter.aenitto.rooms.dto.response;

import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Relation;
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

    public static RoomDetailResponse buildProcessingResponse(Room room, Relation relation, MemberRoom memberRoom, boolean didView, Mission mission, int messageCount) {
        return RoomDetailResponse.builder()
                .room(RoomDetail.of(room))
                .manittee(ManitteeInfo.of(relation))
                .mission(MissionInfo.of(mission))
                .didViewRoulette(didView)
                .admin(memberRoom.isAdmin())
                .messages(new MessageInfo(messageCount))
                .build();
    }

    public static RoomDetailResponse buildPostResponse(Room room, Relation relation, MemberRoom memberRoom, int messageCount) {
        return RoomDetailResponse.builder()
                .room(RoomDetail.of(room))
                .admin(memberRoom.isAdmin())
                .manittee(ManitteeInfo.of(relation))
                .messages(new MessageInfo(messageCount))
                .build();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class ManitteeInfo {
        private final String nickname;

        public static ManitteeInfo of(Relation relation) {
            return ManitteeInfo.builder()
                    .nickname(relation.getManittee().getNickname())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class MissionInfo {
        private final Long id;
        private final String content;

        public static MissionInfo of(Mission mission) {
            return MissionInfo.builder()
                    .id(mission.getId())
                    .content(mission.getContent())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class MessageInfo {
        private final int count;
    }
}
