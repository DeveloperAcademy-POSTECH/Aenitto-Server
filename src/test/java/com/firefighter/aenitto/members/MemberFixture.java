package com.firefighter.aenitto.members;

import com.firefighter.aenitto.members.domain.Member;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

public class MemberFixture {
    public static final Member MEMBER_1 = Member.builder()
            .id(UUID.randomUUID())
            .nickname("LEO")
            .socialId("socialId")
            .build();
}
