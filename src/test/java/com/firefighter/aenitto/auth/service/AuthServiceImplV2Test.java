package com.firefighter.aenitto.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import static com.firefighter.aenitto.members.MemberFixture.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.firefighter.aenitto.auth.client.ClientProxy;
import com.firefighter.aenitto.auth.domain.RefreshToken;
import com.firefighter.aenitto.auth.dto.request.LoginRequestV2;
import com.firefighter.aenitto.auth.dto.response.LoginResponse;
import com.firefighter.aenitto.auth.repository.RefreshTokenRepository;
import com.firefighter.aenitto.auth.token.Token;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AuthServiceImplV2Test {

	private final UUID memberId = UUID.fromString("b48617b2-090d-4ee6-9033-b99f99d98304");
	private final String socialId = "socialId";

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private AuthServiceImplV2 target;

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

	// TODO: 공통으로 사용되는 메서드 클래스로 빼기(2022.11.08)
	private Token token() {
		return Token.builder().accessToken("accessToken").refreshToken("refreshToken").build();
	}

	private Member member() {
		Member member = Member.builder()
			.socialId("socialId")
			.build();
		ReflectionTestUtils.setField(member, "id", UUID.fromString("b48617b2-090d-4ee6-9033-b99f99d98304"));
		return member;
	}

	private RefreshToken refreshToken() {
		return RefreshToken.builder()
			.memberId(UUID.fromString("b48617b2-090d-4ee6-9033-b99f99d98304"))
			.refreshToken("refreshToken입니다123123")
			.build();
	}

	@Test
	@DisplayName("logIn - 성공")
	public void logInV2_success() {

		// given
		doReturn(socialId).when(clientProxy)
			.validateToken(anyString());
		doReturn(token()).when(tokenService).generateToken(socialId, "USER");
		doReturn(Optional.ofNullable(refreshToken())).when(refreshTokenRepository)
			.findByMemberId(member().getId());
		doReturn(Optional.ofNullable(member())).when(memberRepository)
			.findBySocialId(anyString());
		LoginRequestV2 loginRequest = LoginRequestV2.builder().identityToken("access").fcmToken("fcm").build();

		// when
		final LoginResponse result = target.loginOrSignInV2(loginRequest);

		//then
		assertThat(result.getIsNewMember()).isFalse();
		assertThat(result.getUserSettingDone()).isFalse();
	}
}
