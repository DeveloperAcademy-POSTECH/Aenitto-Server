package com.firefighter.aenitto.auth.service;

import com.firefighter.aenitto.auth.client.ClientProxy;
import com.firefighter.aenitto.auth.domain.RefreshToken;
import com.firefighter.aenitto.auth.dto.request.LoginRequest;
import com.firefighter.aenitto.auth.dto.response.LoginResponse;
import com.firefighter.aenitto.auth.repository.RefreshTokenRepository;
import com.firefighter.aenitto.auth.token.Token;
import com.firefighter.aenitto.common.exception.auth.FailedToFetchPublicKeyException;
import com.firefighter.aenitto.common.exception.auth.InvalidIdentityTokenException;
import com.firefighter.aenitto.common.exception.room.RoomNotParticipatingException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

    @BeforeEach
    void setup() {
        this.refreshToken = RefreshToken.builder()
                .memberId(UUID.fromString("b48617b2-090d-4ee6-9033-b99f99d98304"))
                .refreshToken("refreshToken입니다123123")
                .build();

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
