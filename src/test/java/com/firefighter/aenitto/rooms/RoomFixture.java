package com.firefighter.aenitto.rooms;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

public class RoomFixture {
    public static Room roomFixture1() {
        return baseRoomFixture(1, 10, 20, RoomState.PROCESSING);
    }

    public static Room roomFixture2() {
        return baseRoomFixture(2, 10, 20, RoomState.PRE);
    }

    public static MemberRoom memberRoomFixture1(Member member, Room room) {
        MemberRoom memberRoom = baseMemberRoomFixture(1);
        memberRoom.setMemberRoom(member, room);
        return memberRoom;
    }

    public static MemberRoom memberRoomFixture2(Member member, Room room) {
        MemberRoom memberRoom = baseMemberRoomFixture(2);
        memberRoom.setMemberRoom(member, room);
        return memberRoom;
    }
    public static MemberRoom memberRoomFixture3(Member member, Room room) {
        MemberRoom memberRoom = baseMemberRoomFixture(3);
        memberRoom.setMemberRoom(member, room);
        return memberRoom;
    }
    public static MemberRoom memberRoomFixture4(Member member, Room room) {
        MemberRoom memberRoom = baseMemberRoomFixture(4);
        memberRoom.setMemberRoom(member, room);
        return memberRoom;
    }
    public static MemberRoom memberRoomFixture5(Member member, Room room) {
        MemberRoom memberRoom = baseMemberRoomFixture(5);
        memberRoom.setMemberRoom(member, room);
        return memberRoom;
    }

    private static MemberRoom baseMemberRoomFixture(int number) {
        MemberRoom memberRoom = transientMemberRoomFixture(number);
        ReflectionTestUtils.setField(memberRoom, "id", number * 1L);
        return memberRoom;
    }

    private static Room baseRoomFixture(int number, int capacity, int date, RoomState state) {
        Room room = transientRoomFixture(number, capacity, date);
        room.setState(state);
        ReflectionTestUtils.setField(room, "id", 1L * number);
        return room;
    }

    public static Room transientRoomFixture(int number, int capacity, int date) {
        return Room.builder()
                .title("방제목" + number)
                .capacity(capacity)
                .startDate(LocalDate.of(2022, 7, date))
                .endDate(LocalDate.of(2022, 7, date + 1))
                .build();
    }

    public static MemberRoom transientMemberRoomFixture(int number) {
        return MemberRoom.builder()
                .colorIdx(number)
                .build();
    }
}
