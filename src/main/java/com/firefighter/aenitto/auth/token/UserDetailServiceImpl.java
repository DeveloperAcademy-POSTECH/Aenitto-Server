package com.firefighter.aenitto.auth.token;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public CurrentUserDetails loadUserByUsername(String socialId) {
        Member member = memberRepository.findBySocialId(socialId)
                .orElseThrow(()-> new UsernameNotFoundException(socialId));
        return CurrentUserDetails.of(member);
    }
}
