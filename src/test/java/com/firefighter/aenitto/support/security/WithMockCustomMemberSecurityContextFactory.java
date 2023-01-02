package com.firefighter.aenitto.support.security;

import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.util.ReflectionTestUtils;

import com.firefighter.aenitto.auth.token.CurrentUserDetails;
import com.firefighter.aenitto.members.domain.Member;

public class WithMockCustomMemberSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomMember> {

	@Override
	public SecurityContext createSecurityContext(WithMockCustomMember customMember) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		CurrentUserDetails principal = CurrentUserDetails.of(createMember("nickname"));
		Authentication auth = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(),
			principal.getAuthorities());
		context.setAuthentication(auth);
		return context;
	}

	private Member createMember(String nickname) {
		Member member = Member.builder()
			.nickname(nickname)
			.socialId("socialId")
			.build();
		ReflectionTestUtils.setField(member, "id", UUID.fromString("f383cdb3-a871-4410-b146-fb1f7b447b9e"));
		return member;

		//        return Member.builder()
		//                .id()
		//                .nickname(nickname)
		//                .socialId("socialId")
		//                .build();
	}
}
