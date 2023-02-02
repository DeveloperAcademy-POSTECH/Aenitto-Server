package com.firefighter.aenitto.rooms.domain;

import org.springframework.test.util.ReflectionTestUtils;

import com.firefighter.aenitto.members.domain.Member;

public class RelationFixture {
	public static Relation relationFixture(Member manitto, Member manittee, Room room) {
		Relation relation = new Relation();
		ReflectionTestUtils.setField(relation, "manitto", manitto);
		ReflectionTestUtils.setField(relation, "manittee", manittee);
		ReflectionTestUtils.setField(relation, "room", room);
		return relation;
	}
}
