package com.firefighter.aenitto.members.repository;

import com.firefighter.aenitto.members.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

  Optional<Member> findBySocialId(String socialId);
}
