package com.firefighter.aenitto.auth.service;


import com.firefighter.aenitto.auth.domain.RefreshToken;
import com.firefighter.aenitto.auth.dto.request.ReissueTokenRequest;
import com.firefighter.aenitto.auth.dto.request.TempLoginRequest;
import com.firefighter.aenitto.auth.dto.response.ReissueTokenResponse;
import com.firefighter.aenitto.auth.dto.response.TempLoginResponse;
import com.firefighter.aenitto.auth.repository.RefreshTokenRepository;
import com.firefighter.aenitto.auth.repository.RefreshTokenRepositoryImpl;
import com.firefighter.aenitto.auth.token.Token;
import com.firefighter.aenitto.common.exception.auth.InvalidTokenException;
import com.firefighter.aenitto.common.exception.auth.InvalidUserTokenException;
import com.firefighter.aenitto.common.exception.member.MemberNotFoundException;
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
    public TempLoginResponse loginOrSignIn(TempLoginRequest tempLoginRequest) {
        Member member = saveMember(tempLoginRequest.getAccessToken());
        Token token = saveRefreshToken(member);
        return TempLoginResponse.from(token);
    }

    @Override
    public ReissueTokenResponse reissueAccessToken(ReissueTokenRequest reissueTokenRequest) {
        Long refreshTokenTime = tokenService.verifyRefreshToken(reissueTokenRequest.getRefreshToken());
        String socialId = tokenService.getSocialId(reissueTokenRequest.getAccessToken());
        Member member = memberRepository.findBySocialId(socialId).orElseThrow(MemberNotFoundException::new);
        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(member.getId())
                .orElseThrow(InvalidTokenException::new);

        if (!refreshToken.getRefreshToken().equals(reissueTokenRequest.getRefreshToken())) {
            throw new InvalidUserTokenException();
        }

        Token token = tokenService.generateToken(socialId, "USER");
        if (refreshTokenTime < 1000L * 60L * 60L * 24L * 3L) {
            refreshToken.updateRefreshToken(token.getRefreshToken());

            return ReissueTokenResponse.builder()
                    .accessToken(token.getAccessToken())
                    .refreshToken(token.getRefreshToken())
                    .build();
        } else {
            return ReissueTokenResponse.builder()
                    .accessToken(token.getAccessToken())
                    .refreshToken(reissueTokenRequest.getRefreshToken())
                    .build();
        }
    }

    public Token saveRefreshToken(Member member) {
        Token token = tokenService.generateToken(member.getSocialId(), "USER");

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
