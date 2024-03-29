package com.firefighter.aenitto.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import com.firefighter.aenitto.auth.token.Token;
import com.firefighter.aenitto.members.domain.Member;

@Getter
@Builder
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class LoginResponse {

	private final String accessToken;
	private final String refreshToken;
	private final String nickname;
	private final Boolean isNewMember;
	private final Boolean userSettingDone;

	public static LoginResponse from(Token token, Member member) {
		return LoginResponse.builder()
			.accessToken(token.getAccessToken())
			.build();
	}
}
