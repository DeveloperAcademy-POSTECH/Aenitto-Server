package com.firefighter.aenitto.rooms.dto;


import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.dto.response.GetRoomStateResponse;
import com.firefighter.aenitto.rooms.dto.response.ParticipatingRoomsResponse;
import com.firefighter.aenitto.rooms.dto.response.RoomParticipantsResponse;

import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.dto.response.RoomDetailResponse;

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


    public static RoomParticipantsResponse roomParticipantsResponse(List<MemberRoom> memberRooms) {
        return RoomParticipantsResponse.of(memberRooms);
    }

    public static RoomDetailResponse roomDetailResponse(Room room, Relation relation, Mission mission) {
        return RoomDetailResponse.builder()
                .room(RoomDetailResponse.RoomDetail.of(room))
                .manittee(RoomDetailResponse.ManitteeInfo.of(relation))
                .participants(RoomDetailResponse.ParticipantsInfo.of(room.getMemberRooms()))
                .messages(new RoomDetailResponse.MessageInfo(3))
                .admin(false)
                .didViewRoulette(false)
                .mission(RoomDetailResponse.MissionInfo.of(mission))
                .build();
    }
}
