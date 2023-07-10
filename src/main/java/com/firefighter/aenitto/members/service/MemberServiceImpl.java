package com.firefighter.aenitto.members.service;

import com.firefighter.aenitto.common.exception.member.MemberNotFoundException;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.domain.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Qualifier(value = "memberServiceImpl")
@Service
@Transactional
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;

  @Override
  @Transactional
  public void setNickname(Member member, String nickName) {
    memberRepository.findById(member.getId())
        .orElseThrow(MemberNotFoundException::new);

    member.changeNickname(nickName);
  }

  @Override
  @Transactional
  public void withdrawal(Member member) {
    member.getMemberRooms()
        .forEach(memberRoom -> {
          Room room = memberRoom.getRoom();
          room.removeMember(memberRoom);
          room.clearRelations();
          Relation.createRelations(room);
        });
    member.withdrawl(true);
  }
}
