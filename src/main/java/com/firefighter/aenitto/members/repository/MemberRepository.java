package com.firefighter.aenitto.members.repository;

import com.firefighter.aenitto.members.domain.Member;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, UUID> {

  Optional<Member> findBySocialId(String socialId);
}
