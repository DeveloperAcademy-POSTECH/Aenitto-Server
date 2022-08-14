package com.firefighter.aenitto.auth.service;


import com.firefighter.aenitto.auth.domain.RefreshToken;
import com.firefighter.aenitto.auth.dto.request.TempLoginRequest;
import com.firefighter.aenitto.auth.dto.response.TempLoginResponse;
import com.firefighter.aenitto.auth.repository.RefreshTokenRepository;
import com.firefighter.aenitto.auth.repository.RefreshTokenRepositoryImpl;
import com.firefighter.aenitto.auth.token.Token;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Qualifier(value = "authServiceImpl")
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Qualifier("RefreshTokenRepositoryImpl")
    private final RefreshTokenRepository refreshTokenRepository;
    @Qualifier("memberRepositoryImpl")
    private final MemberRepository memberRepository;
    @Autowired
    private final TokenService tokenService;

    @Override
    public TempLoginResponse loginOrSignIn(TempLoginRequest tempLoginRequest){
        Member member = saveMember(tempLoginRequest.getAccessToken());
        Token token = saveRefreshToken(member);
        return TempLoginResponse.from(token);
    }

    public Token saveRefreshToken(Member member){
        System.out.println(member.getSocialId());
        Token token  = tokenService.generateToken(member.getSocialId(), "USER");

        final RefreshToken result = refreshTokenRepository
                .saveRefreshToken(RefreshToken.builder()
                        .memberId(member.getId()).refreshToken(token.getRefreshToken()).build());
        return token;
    }


    public Member saveMember(String socialId) {
        final Member result = memberRepository
                .saveMember(Member.builder().socialId(socialId).build());
        return result;
    }
}
