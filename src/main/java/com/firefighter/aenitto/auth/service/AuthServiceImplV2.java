package com.firefighter.aenitto.auth.service;

import com.firefighter.aenitto.auth.client.ClientProxy;
import com.firefighter.aenitto.auth.domain.RefreshToken;
import com.firefighter.aenitto.auth.dto.request.LoginRequestV2;
import com.firefighter.aenitto.auth.dto.response.LoginResponse;
import com.firefighter.aenitto.auth.repository.RefreshTokenRepository;
import com.firefighter.aenitto.auth.token.Token;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Qualifier(value = "authServiceImplV2 ")
@Transactional
@RequiredArgsConstructor
public class AuthServiceImplV2 implements AuthServiceV2 {

  @Qualifier("RefreshTokenRepositoryImpl")
  private final RefreshTokenRepository refreshTokenRepository;

  private final MemberRepository memberRepository;
  @Autowired
  private final TokenService tokenService;

  private final ClientProxy clientProxy;

  @Transactional
  public LoginResponse loginOrSignInV2(LoginRequestV2 loginRequest) {
    String socialId = clientProxy.validateToken(loginRequest.getIdentityToken());
    Optional<Member> member = memberRepository.findBySocialId(socialId);
    if (member.isEmpty()) {
      return signIn(socialId, loginRequest.getFcmToken());
    } else {
      return logIn(socialId, member.get(), loginRequest.getFcmToken());
    }
  }

  private LoginResponse signIn(String socialId, String fcmToken) {
    Member member = memberRepository
      .save(Member.builder().socialId(socialId).fcmToken(fcmToken).build());
    Token token = tokenService.generateToken(member.getSocialId(), "USER");

    RefreshToken refreshToken = refreshTokenRepository
      .saveRefreshToken(RefreshToken.builder()
        .refreshToken(token.getRefreshToken())
        .memberId(member.getId()).build());

    return LoginResponse.builder().accessToken(token.getAccessToken())
      .nickname(member.getNickname())
      .refreshToken(token.getRefreshToken()).isNewMember(true)
      .userSettingDone(false).build();
  }

  private LoginResponse logIn(String socialId, Member member, String fcmToken) {
    member.setFcmToken(fcmToken);
    member.recovery();
    memberRepository.save(member);

    Token token = tokenService.generateToken(member.getSocialId(), "USER");

    RefreshToken refreshToken = refreshTokenRepository.findByMemberId(member.getId()).orElseThrow();
    refreshToken.updateRefreshToken(token.getRefreshToken());

    return LoginResponse.builder().accessToken(token.getAccessToken())
      .nickname(member.getNickname())
      .refreshToken(token.getRefreshToken()).isNewMember(false)
      .userSettingDone(member.getNickname() != null).build();
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
      .save(Member.builder().socialId(socialId).build());
    return result;
  }
}

