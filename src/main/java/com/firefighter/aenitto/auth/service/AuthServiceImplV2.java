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
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
    String identityToken = loginRequest.getIdentityToken();
    String socialId = clientProxy.validateToken(identityToken);
    String fcmToken = loginRequest.getFcmToken();

    Member member = memberRepository.findBySocialId(socialId)
      .orElseGet(() -> signIn(socialId, fcmToken));

    return logIn(member, fcmToken);
  }

  @NotNull
  private Member signIn(String socialId, String fcmToken) {
    Token token = tokenService.generateToken(socialId, "USER");
    Member newMember = Member.builder()
      .socialId(socialId)
      .fcmToken(fcmToken)
      .build();

    String refreshTokenValue = token.getRefreshToken();
    UUID memberId = newMember.getId();
    createRefreshToken(refreshTokenValue, memberId);

    return memberRepository.save(newMember);
  }

  private LoginResponse logIn(Member member, String fcmToken) {
    UUID memberId = member.getId();

    String socialId = member.getSocialId();
    Token token = tokenService.generateToken(socialId, "USER");

    member.setFcmToken(fcmToken);
    memberRepository.save(member);

    String refreshTokenValue = token.getRefreshToken();
    refreshTokenRepository.findByMemberId(memberId)
      .ifPresentOrElse(
        refreshToken -> refreshToken.updateRefreshToken(refreshTokenValue),
        () -> createRefreshToken(refreshTokenValue, memberId)
      );

    String nickname = member.getNickname();
    LoginResponse.LoginResponseBuilder builder = LoginResponse.builder()
      .accessToken(token.getAccessToken())
      .nickname(nickname)
      .refreshToken(refreshTokenValue)
      .isNewMember(member.getNickname() != null)
      .userSettingDone((member.getNickname() != null) && (!member.getNickname().isEmpty()));

    if (member.isWithdrawal()) {
      builder
        .userSettingDone(false)
        .isNewMember(true);
      member.recovery();
    }

    return builder.build();
  }

  private void createRefreshToken(String refreshTokenValue, UUID memberId) {
    RefreshToken refreshToken = RefreshToken.builder()
      .refreshToken(refreshTokenValue)
      .memberId(memberId)
      .build();
    refreshTokenRepository.saveRefreshToken(refreshToken);
  }
}

