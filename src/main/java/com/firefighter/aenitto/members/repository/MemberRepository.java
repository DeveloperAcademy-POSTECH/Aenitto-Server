package com.firefighter.aenitto.members.repository;

import java.util.Optional;
import java.util.UUID;

import com.firefighter.aenitto.members.domain.Member;

public interface MemberRepository {
	public Optional<Member> findByMemberId(UUID memberId);

	public Member updateMember(Member member);

	public Member saveMember(Member member);

	public Optional<Member> findBySocialId(String socialId);
}
