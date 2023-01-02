package com.firefighter.aenitto.members.repository;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.firefighter.aenitto.members.domain.Member;

@Repository
@Qualifier(value = "memberRepositoryImpl")
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
	private final EntityManager em;

	@Override
	public Optional<Member> findByMemberId(UUID memberId) {
		return Optional.ofNullable(em.find(Member.class, memberId));
	}

	@Override
	public Member updateMember(Member member) {
		return em.merge(member);
	}

	@Override
	public Member saveMember(Member member) {
		em.persist(member);
		return member;
	}

	@Override
	public Optional<Member> findBySocialId(String socialId) {
		return em.createQuery("SELECT m FROM Member m WHERE m.socialId = :socialId", Member.class)
			.setParameter("socialId", socialId)
			.getResultList().stream().findFirst();
	}
}
