package com.firefighter.aenitto.auth.service;

import org.springframework.stereotype.Service;

import com.firefighter.aenitto.auth.dto.request.ReissueTokenRequest;
import com.firefighter.aenitto.auth.dto.response.ReissueTokenResponse;

import com.firefighter.aenitto.auth.dto.request.LoginRequest;
import com.firefighter.aenitto.auth.dto.response.LoginResponse;

@Service
public interface AuthService {
	ReissueTokenResponse reissueAccessToken(ReissueTokenRequest reissueTokenRequest);

	LoginResponse loginOrSignIn(LoginRequest tempLoginRequest);
}
