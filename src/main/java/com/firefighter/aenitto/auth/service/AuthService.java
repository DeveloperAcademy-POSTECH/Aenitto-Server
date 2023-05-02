package com.firefighter.aenitto.auth.service;

import org.springframework.stereotype.Service;

import com.firefighter.aenitto.auth.dto.request.ReissueTokenRequest;
import com.firefighter.aenitto.auth.dto.request.WithdrawlRequest;
import com.firefighter.aenitto.auth.dto.response.ReissueTokenResponse;

import com.firefighter.aenitto.auth.dto.request.LoginRequest;
import com.firefighter.aenitto.auth.dto.response.LoginResponse;
import com.firefighter.aenitto.members.domain.Member;

@Service
public interface AuthService {
	ReissueTokenResponse reissueAccessToken(ReissueTokenRequest reissueTokenRequest);

	LoginResponse loginOrSignIn(LoginRequest tempLoginRequest);
	void withdrawlUser(Member member, WithdrawlRequest withdrawlRequest);
}
