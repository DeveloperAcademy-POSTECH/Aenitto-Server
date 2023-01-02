package com.firefighter.aenitto.auth.token;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;

@RequiredArgsConstructor
@Service
@Transactional
public class UserDetailServiceImpl implements UserDetailsService {
	private final MemberRepository memberRepository;

	@Override
	public CurrentUserDetails loadUserByUsername(String socialId) {
		Member member = memberRepository.findBySocialId(socialId)
			.orElseThrow(() -> new UsernameNotFoundException(socialId));
		return CurrentUserDetails.of(member);
	}
}
