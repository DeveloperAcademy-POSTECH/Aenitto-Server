package com.firefighter.aenitto.members;

import com.firefighter.aenitto.members.domain.Member;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

public class MemberFixture {
    public static Member memberFixture() {
        return baseMemberFixture(1);
    }

    public static Member memberFixture2() {
        return baseMemberFixture(2);
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
