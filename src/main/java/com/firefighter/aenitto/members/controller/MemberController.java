package com.firefighter.aenitto.members.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import com.firefighter.aenitto.common.annotation.CurrentMember;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.dto.request.ChangeNicknameRequest;
import com.firefighter.aenitto.members.service.MemberService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

	@Qualifier("memberServiceImpl")
	private final MemberService memberService;

	@PutMapping("/members/nickname")
	public ResponseEntity changeNickname(
		@Valid @RequestBody final ChangeNicknameRequest changeNicknameRequest,
		@CurrentMember Member member
	) throws Exception {
		memberService.setNickname(member, changeNicknameRequest.getNickname());
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/members")
	public ResponseEntity<Void> withdrawal(@CurrentMember Member member) {
		memberService.withdrawal(member);
		return ResponseEntity.noContent().build();
	}
}
