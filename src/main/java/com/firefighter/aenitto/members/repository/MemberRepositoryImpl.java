package com.firefighter.aenitto.members.repository;

import com.firefighter.aenitto.members.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;

@Repository
@Qualifier(value = "memberRepositoryImpl")
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final EntityManager em;

    @Override
    public Member findByMemberId(UUID memberId) {
        return em.find(Member.class, memberId);
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
        return Optional.ofNullable(em.createQuery("SELECT m FROM Member m WHERE m.socialId = :socialId", Member.class)
                .setParameter("socialId" , socialId)
                .getSingleResult());
    }
}
