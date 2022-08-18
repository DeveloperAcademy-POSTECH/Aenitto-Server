package com.firefighter.aenitto.rooms.domain;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.firefighter.aenitto.rooms.domain.Relation.createRelations;


public class RelationFixture {
    public static Relation relationFixture(Member manitto, Member manittee, Room room) {
        Relation relation = new Relation();
        ReflectionTestUtils.setField(relation, "manitto", manitto);
        ReflectionTestUtils.setField(relation, "manittee", manittee);
        ReflectionTestUtils.setField(relation, "room", room);
        return relation;
    }
}
