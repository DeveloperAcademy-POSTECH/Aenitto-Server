package com.firefighter.aenitto.rooms.dto.response;

import com.firefighter.aenitto.rooms.domain.MemberRoom;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Builder
public class RoomParticipantsResponse {

    private final int count;
    private final List<RoomParticipants> members;

    public static RoomParticipantsResponse of(List<MemberRoom> memberRooms){
        return RoomParticipantsResponse.builder()
                .count(memberRooms.size())
                .members(memberRooms.stream().map(RoomParticipants::of)
                        .collect(Collectors.toList())).build();
    }

    @RequiredArgsConstructor
    @Getter
    @Builder
    public static class RoomParticipants{
        private final String nickname;
        private final int colorIdx;

        public static RoomParticipants of(MemberRoom memberRoom){
            return RoomParticipants.builder()
                    .colorIdx(memberRoom.getColorIdx())
                    .nickname(memberRoom.getMember().getNickname())
                    .build();
        }
    }

}