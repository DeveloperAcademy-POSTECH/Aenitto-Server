package com.firefighter.aenitto.members;

import java.util.UUID;

import org.springframework.test.util.ReflectionTestUtils;

import com.firefighter.aenitto.members.domain.Member;

public class MemberFixture {
	public static Member memberFixture() {
		return baseMemberFixture(1);
	}

	public static Member memberFixture2() {
		return baseMemberFixture(2);
	}

	public static Member memberFixture3() {
		return baseMemberFixture(3);
	}

	public static Member memberFixture4() {
		return baseMemberFixture(4);
	}

	public static Member memberFixture5() {
		return baseMemberFixture(5);
	}

	private static Member baseMemberFixture(int number) {
		Member member = transientMemberFixture(number);
		ReflectionTestUtils.setField(member, "id", UUID.randomUUID());
		return member;
	}

	public static Member transientMemberFixture(int number) {
		return Member.builder()
			.nickname("멤버" + number)
			.build();
	}
}
