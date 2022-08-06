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

//        UsernamePasswordAuthenticationToken authentication =
//                new UsernamePasswordAuthenticationToken(
//                        CurrentUserDetails.of(createMember(customMember.id(), customMember.name())),
//                        null
////                        createAuthorityList(customMember.role().getRole()));
//                );

        CurrentUserDetails principal = CurrentUserDetails.of(createMember(customMember.name()));
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }

    private Member createMember(String nickname) {
        return Member.builder()
                .nickname(nickname)
                .socialId("socialId")
                .build();
    }
}
