package com.firefighter.aenitto.auth.service;

import com.firefighter.aenitto.auth.client.ClientProxy;
import com.firefighter.aenitto.auth.domain.RefreshToken;
import com.firefighter.aenitto.auth.dto.request.ReissueTokenRequest;
import com.firefighter.aenitto.auth.dto.response.ReissueTokenResponse;
import com.firefighter.aenitto.auth.repository.RefreshTokenRepository;
import com.firefighter.aenitto.auth.token.Token;
import com.firefighter.aenitto.common.exception.auth.InvalidTokenException;
import com.firefighter.aenitto.common.exception.auth.InvalidUserTokenException;
import com.firefighter.aenitto.common.exception.member.MemberNotFoundException;

import com.firefighter.aenitto.auth.dto.request.LoginRequest;
import com.firefighter.aenitto.auth.dto.response.LoginResponse;

import com.firefighter.aenitto.common.exception.auth.FailedToFetchPublicKeyException;
import com.firefighter.aenitto.common.exception.auth.InvalidIdentityTokenException;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.firefighter.aenitto.members.MemberFixture.memberFixture;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


@ExtendWith(MockitoExtension.class)
@Transactional
public class AuthServiceImplTest {

    private final UUID memberId = UUID.fromString("b48617b2-090d-4ee6-9033-b99f99d98304");
    private final String socialId = "socialId";

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AuthServiceImpl target;

    @Mock
    private ClientProxy clientProxy;

    @Mock
    private TokenService tokenService;

    private RefreshToken refreshToken;

    @Mock
    private Token token;

    Member member;

    @BeforeEach
    void setup() {
        this.refreshToken = RefreshToken.builder()
                .memberId(UUID.fromString("b48617b2-090d-4ee6-9033-b99f99d98304"))
                .refreshToken("refreshToken입니다123123")
                .build();
        member = memberFixture();
    }


    @Test
    @DisplayName("토큰 재발급 실패 / 유효하지 않은 refresh token ")
    public void reissue_refreshToken_failure_refreshToken_not_valid() {

        // given
        when(tokenService.verifyRefreshToken(anyString())).thenThrow(InvalidTokenException.class);
        ReissueTokenRequest reissueTokenRequest = ReissueTokenRequest
                .builder().accessToken("invalidToken").refreshToken("invalidRefresh")
                .build();

        // when, then
        assertThatExceptionOfType(InvalidTokenException.class)
                .isThrownBy(() -> {
                    target.reissueAccessToken(reissueTokenRequest);
                });
    }

    @Test
    @DisplayName("토큰 재발급 실패 / 사용자 정보 찾을 수 없음")
    public void reissue_refreshToken_failure_user_not_found() {

        // given
        when(tokenService.verifyRefreshToken(anyString())).thenReturn(1L);
        when(tokenService.getSocialId(anyString())).thenReturn("wrong");
        ReissueTokenRequest reissueTokenRequest = ReissueTokenRequest
                .builder().accessToken("invalidToken").refreshToken("invalidRefresh")
                .build();

        // when, then
        assertThatExceptionOfType(MemberNotFoundException.class)
                .isThrownBy(() -> {
                    target.reissueAccessToken(reissueTokenRequest);
                });
    }

    @Test
    @DisplayName("토큰 재발급 실패 / 사용자 정보 찾을 수 없음")
    public void reissue_refreshToken_failure_user_logged_out() {

        // given
         ReissueTokenRequest reissueTokenRequest = ReissueTokenRequest
                        .builder().accessToken("invalidToken").refreshToken("invalidRefresh")
                        .build();
        when(tokenService.verifyRefreshToken(anyString())).thenReturn(1L);
        when(tokenService.getSocialId(anyString())).thenReturn("socialId");
        when(memberRepository.findBySocialId(anyString())).thenReturn(Optional.ofNullable(member));
        when(refreshTokenRepository.findByMemberId(any(UUID.class))).thenThrow(InvalidTokenException.class);

        // when, then
        assertThatExceptionOfType(InvalidTokenException.class)
                .isThrownBy(() -> {
                    target.reissueAccessToken(reissueTokenRequest);
                });
    }

    @Test
    @DisplayName("토큰 재발급 실패 / 잘못된 사용자")
    public void reissue_refreshToken_failure_wrong_user() {

        // given
        ReissueTokenRequest reissueTokenRequest = ReissueTokenRequest
                .builder().accessToken("invalidToken").refreshToken("invalidRefresh")
                .build();
        when(tokenService.verifyRefreshToken(anyString())).thenReturn(1L);
        when(tokenService.getSocialId(anyString())).thenReturn("socialId");
        when(memberRepository.findBySocialId(anyString())).thenReturn(Optional.ofNullable(member));
        when(refreshTokenRepository.findByMemberId(any(UUID.class))).thenReturn(Optional.of(refreshToken()));

        // when, then
        assertThatExceptionOfType(InvalidUserTokenException.class)
                .isThrownBy(() -> {
                    target.reissueAccessToken(reissueTokenRequest);
                });
    }

    @Test
    @DisplayName("토큰 재발급 / 성공 - accesstoken만 재발급")
    public void reissue_token_success_only_accessToken() {
        // given
        ReissueTokenRequest reissueTokenRequest = ReissueTokenRequest
                .builder().accessToken("validToken").refreshToken("refreshToken입니다123123")
                .build();

        when(tokenService.verifyRefreshToken(anyString())).thenReturn(1000L * 60L * 60L * 24L * 9L);
        when(tokenService.getSocialId(anyString())).thenReturn("socialId");
        when(memberRepository.findBySocialId(anyString())).thenReturn(Optional.ofNullable(member));
        when(refreshTokenRepository.findByMemberId(any(UUID.class))).thenReturn(Optional.of(refreshToken()));
        when(tokenService.generateToken(anyString(), eq("USER"))).thenReturn(token());

        // when
        ReissueTokenResponse reissueTokenResponse = target.reissueAccessToken(reissueTokenRequest);

        //then
        assertThat(reissueTokenResponse.getAccessToken()).isNotEqualTo(reissueTokenRequest.getAccessToken());
        assertThat(reissueTokenResponse.getRefreshToken()).isEqualTo(reissueTokenRequest.getRefreshToken());
    }

    @Test
    @DisplayName("토큰 재발급 / 성공 -  accessToken, refreshToken재발급")
    public void reissue_token_success_accessToken_and_refreshToken() {
        // given
        ReissueTokenRequest reissueTokenRequest = ReissueTokenRequest
                .builder().accessToken("validToken").refreshToken("refreshToken입니다123123")
                .build();

        when(tokenService.verifyRefreshToken(anyString())).thenReturn(1000L * 60L * 60L * 24L);
        when(tokenService.getSocialId(anyString())).thenReturn("socialId");
        when(memberRepository.findBySocialId(anyString())).thenReturn(Optional.ofNullable(member));
        when(refreshTokenRepository.findByMemberId(any(UUID.class))).thenReturn(Optional.of(refreshToken()));
        when(tokenService.generateToken(anyString(), eq("USER"))).thenReturn(token());

        // when
        ReissueTokenResponse reissueTokenResponse = target.reissueAccessToken(reissueTokenRequest);

        //then
        assertThat(reissueTokenResponse.getAccessToken()).isNotEqualTo(reissueTokenRequest.getAccessToken());
        assertThat(reissueTokenResponse.getRefreshToken()).isNotEqualTo(reissueTokenRequest.getRefreshToken());
    }


    private Member member() {
        Member member = Member.builder()
                .socialId("socialId")
                .build();
        ReflectionTestUtils.setField(member, "id", UUID.fromString("b48617b2-090d-4ee6-9033-b99f99d98304"));
        return member;
    }

    private Token token() {
        return Token.builder().accessToken("accessToken").refreshToken("refreshToken").build();
    }


    private RefreshToken refreshToken() {
        return RefreshToken.builder()
                .memberId(UUID.fromString("b48617b2-090d-4ee6-9033-b99f99d98304"))
                .refreshToken("refreshToken입니다123123")
                .build();
    }

    @Test
    @DisplayName("signIn - 성공")
    public void signIn_success() {

        // given
        doReturn(socialId).when(clientProxy)
                .validateToken(anyString());
        doReturn(token()).when(tokenService).generateToken(socialId, "USER");
        doReturn(Optional.empty()).when(memberRepository)
                .findBySocialId(anyString());
        doReturn(member()).when(memberRepository)
                .saveMember(any(Member.class));
        LoginRequest loginRequest = LoginRequest.builder().identityToken("access").build();

        // when
        final LoginResponse result = target.loginOrSignIn(loginRequest);

        //then
        assertThat(result.getAccessToken()).isNotNull();
        assertAll(
                () -> verify(refreshTokenRepository).saveRefreshToken(any(RefreshToken.class)),
                () -> verify(memberRepository).saveMember(any(Member.class))
        );
    }

    @Test
    @DisplayName("logIn - 성공")
    public void logIn_success() {

        // given
        doReturn(socialId).when(clientProxy)
                .validateToken(anyString());
        doReturn(token()).when(tokenService).generateToken(socialId, "USER");
        doReturn(Optional.ofNullable(refreshToken())).when(refreshTokenRepository)
                .findByMemberId(member().getId());
        doReturn(Optional.ofNullable(member())).when(memberRepository)
                .findBySocialId(anyString());
        LoginRequest loginRequest = LoginRequest.builder().identityToken("access").build();

        // when
        final LoginResponse result = target.loginOrSignIn(loginRequest);

        //then
        assertThat(result.getIsNewMember()).isFalse();
        assertThat(result.getUserSettingDone()).isFalse();

    }

    @Test
    @DisplayName("signUp/logIn - 실패 / 애플 퍼블릭 키 가져오기 실패")
    public void signUpLogin_failure_get_apple_public_key() {

        // given
        doThrow(new FailedToFetchPublicKeyException()).when(clientProxy)
                .validateToken(anyString());
        LoginRequest loginRequest = LoginRequest.builder().identityToken("access").build();

        // when, then
        assertThatExceptionOfType(FailedToFetchPublicKeyException.class)
                .isThrownBy(() -> {
                    target.loginOrSignIn(loginRequest);
                });
    }

    @Test
    @DisplayName("signUp/logIn - 실패 유효한 identityToken이 아님")
    public void signUpLogin_failure_not_valid_identityToken() {

        // given
        doThrow(new InvalidIdentityTokenException()).when(clientProxy)
                .validateToken(anyString());
        LoginRequest loginRequest = LoginRequest.builder().identityToken("access").build();

        // when, then
        assertThatExceptionOfType(InvalidIdentityTokenException.class)
                .isThrownBy(() -> {
                    target.loginOrSignIn(loginRequest);
                });
    }
}
