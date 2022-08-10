package com.firefighter.aenitto.support.security;

import com.firefighter.aenitto.auth.token.CurrentUserDetails;
import com.firefighter.aenitto.members.domain.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.UUID;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

public class WithMockCustomMemberSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomMember> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomMember customMember) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        CurrentUserDetails principal = CurrentUserDetails.of(createMember("nickname"));
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }

    private Member createMember(String nickname) {
        return Member.builder()
                .id(UUID.fromString("f383cdb3-a871-4410-b146-fb1f7b447b9e"))
                .nickname(nickname)
                .socialId("socialId")
                .build();
    }
}
