package com.firefighter.aenitto.auth.service;

import com.firefighter.aenitto.auth.domain.RefreshToken;
import com.firefighter.aenitto.auth.dto.request.TempLoginRequest;
import com.firefighter.aenitto.auth.dto.response.TempLoginResponse;
import com.firefighter.aenitto.auth.repository.RefreshTokenRepository;
import com.firefighter.aenitto.auth.token.Token;
import com.firefighter.aenitto.common.exception.auth.RefreshTokenExistException;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import com.firefighter.aenitto.members.repository.MemberRepositoryImpl;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    private final UUID memberId = UUID.fromString("b48617b2-090d-4ee6-9033-b99f99d98304");
    private final String socialId = "socialId입니다";

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AuthServiceImpl target;

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

    @Test
    @DisplayName("임시 login 성공 - 회원가입")
    public void temp_login() {

        // given
        doReturn(member()).when(memberRepository).saveMember(any(Member.class));
        doReturn(token()).when(tokenService).generateToken("socialId입니다", "USER");
        doReturn(refreshToken()).when(refreshTokenRepository).saveRefreshToken(any(RefreshToken.class));
        TempLoginRequest tempLoginRequest = TempLoginRequest.builder()
                .accessToken("accessToken")
                .build();

        // when
        final TempLoginResponse result = target.loginOrSignIn(tempLoginRequest);

        //then
        assertThat(result.getAccessToken()).isNotNull();

        // then
       assertAll(
               () -> verify(memberRepository).saveMember(any(Member.class))
       );
    }
    private Member member() {
        return Member.builder()
                .id(UUID.fromString("b48617b2-090d-4ee6-9033-b99f99d98304"))
                .socialId("socialId입니다")
                .build();
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
}
