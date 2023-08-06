package com.firefighter.aenitto.members.controller;


import com.firefighter.aenitto.common.annotation.CurrentMember;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.dto.request.ChangeNicknameRequest;
import com.firefighter.aenitto.members.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @PutMapping("/members/nickname")
  public ResponseEntity<Void> changeNickname(
    @Valid @RequestBody final ChangeNicknameRequest changeNicknameRequest,
    @CurrentMember Member member
  ) {
    memberService.setNickname(member, changeNicknameRequest.getNickname());
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/members")
  public ResponseEntity<Void> withdrawal(@CurrentMember Member member) {
    memberService.withdrawal(member.getId());
    return ResponseEntity.noContent().build();
  }
}
