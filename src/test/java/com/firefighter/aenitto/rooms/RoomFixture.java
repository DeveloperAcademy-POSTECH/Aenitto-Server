package com.firefighter.aenitto.rooms;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

public class RoomFixture {
    public static Room roomFixture() {
        Room room = Room.builder()
                .title("방제목")
                .capacity(10)
                .startDate(LocalDate.of(2022, 6, 20))
                .endDate(LocalDate.of(2022, 6, 30))
                .build();
        ReflectionTestUtils.setField(room, "id", 1L);
        ReflectionTestUtils.setField(room, "state", RoomState.PROCESSING);
        return room;
    }

    public static Room roomFixture2() {
        Room room = Room.builder()
                .title("방제목2")
                .capacity(10)
                .startDate(LocalDate.of(2022, 6, 20))
                .endDate(LocalDate.of(2022, 6, 30))
                .build();
        ReflectionTestUtils.setField(room, "id", 3L);
        ReflectionTestUtils.setField(room, "state", RoomState.PRE);
        return room;
    }

    public static MemberRoom memberRoomFixture(Member member, Room room) {
        MemberRoom memberRoom = MemberRoom.builder()
                .admin(false)
                .colorIdx(1)
                .build();
        ReflectionTestUtils.setField(memberRoom, "id", 1L);
        memberRoom.setMemberRoom(member, room);
        return memberRoom;
    }


}
