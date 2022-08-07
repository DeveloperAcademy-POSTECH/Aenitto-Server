package com.firefighter.aenitto.members.service;

import com.firefighter.aenitto.members.domain.Member;

public interface MemberService {
    void setNickname(Member member, String nickname);
}
