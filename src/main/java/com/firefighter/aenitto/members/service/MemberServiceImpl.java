package com.firefighter.aenitto.members.service;


import com.firefighter.aenitto.common.exception.member.MemberNotFoundException;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    @Override
    public void setNickname(Member member, String nickName){
        memberRepository.findByMemberId(member.getId())
                .orElseThrow(MemberNotFoundException::new);

        member.changeNickname(nickName);
        memberRepository.updateMember(member);
    }
}
