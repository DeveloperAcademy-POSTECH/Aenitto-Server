package com.firefighter.aenitto.members.service;


import com.firefighter.aenitto.common.exception.member.MemberNotFoundException;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Qualifier(value = "memberServiceImpl")
@Service
@Transactional
public class MemberServiceImpl implements MemberService {
    @Qualifier("memberRepositoryImpl")
    private final MemberRepository memberRepository;
    @Override
    public void setNickname(Member member, String nickName){
        memberRepository.findByMemberId(member.getId())
                .orElseThrow(MemberNotFoundException::new);

        member.changeNickname(nickName);
        memberRepository.updateMember(member);
    }
}
