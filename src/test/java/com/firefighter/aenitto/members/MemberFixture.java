package com.firefighter.aenitto.members;

import com.firefighter.aenitto.members.domain.Member;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

public class MemberFixture {
    public static Member memberFixture() {
        Member member = Member.builder()
                .nickname("Leo")
                .build();
        ReflectionTestUtils.setField(member, "id", UUID.randomUUID());
        return member;
    }
}
