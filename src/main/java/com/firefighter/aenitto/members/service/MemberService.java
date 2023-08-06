package com.firefighter.aenitto.members.service;

import com.firefighter.aenitto.common.exception.member.MemberNotFoundException;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;

  @Transactional
  public void setNickname(Member member, String nickName) {
    findById(member.getId())
      .ifPresentOrElse(
        (findMember) -> findMember.changeNickname(nickName),
        MemberNotFoundException::new
      );
  }

  @Transactional
  public void withdrawal(UUID memberId) {
    findById(memberId)
      .ifPresentOrElse(
        Member::withdrawal,
        MemberNotFoundException::new
      );
  }

  @NotNull
  private Optional<Member> findById(UUID id) {
    return memberRepository.findById(id);
  }
}
