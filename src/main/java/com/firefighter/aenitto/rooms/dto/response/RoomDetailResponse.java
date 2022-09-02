package com.firefighter.aenitto.rooms.dto.response;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RoomDetailResponse {
    private final RoomDetail room;
    private final ParticipantsInfo participants;
    private final ManitteeInfo manittee;
    private final InvitationInfo invitation;
    private final MissionInfo mission;
    private final Boolean didViewRoulette;
    private final Boolean admin;
    private final MessageInfo messages;

    public static RoomDetailResponse buildPreResponse(Room room, MemberRoom memberRoom) {
        return RoomDetailResponse.builder()
                .room(RoomDetail.of(room))
                .invitation(new InvitationInfo(room.getInvitation()))
                .participants(ParticipantsInfo.of(room.getMemberRooms()))
                .admin(memberRoom.isAdmin())
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
    public static class RoomDetail {
        private final Long id;
        private final String title;
        private final String startDate;
        private final String endDate;
        private final int capacity;
        private final String state;

        public static RoomDetail of (Room room) {
            return RoomDetail.builder()
                    .id(room.getId())
                    .title(room.getTitle())
                    .capacity(room.getCapacity())
                    .startDate(room.getStartDateValue())
                    .endDate(room.getEndDateValue())
                    .state(room.getState().toString())
                    .build();
        }
    }

    @Getter @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class ParticipantsInfo {
        private final int count;
        private final List<MemberInfo> members;

        public static ParticipantsInfo of(List<MemberRoom> memberRooms) {
            return ParticipantsInfo.builder()
                    .count(memberRooms.size())
                    .members(memberRooms
                                    .stream()
                                    .map(MemberInfo::new)
                                    .collect(Collectors.toList()))
                    .build();
        }

        @Getter
        @NoArgsConstructor(force = true)
        public static class MemberInfo {
            private final UUID id;
            private final String nickname;
            public MemberInfo(MemberRoom memberRoom) {
                Member member = memberRoom.getMember();
                this.id = member.getId();
                this.nickname = member.getNickname();
            }
        }
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

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class InvitationInfo {
        private final String code;
    }
}
