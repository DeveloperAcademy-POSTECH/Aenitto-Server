package com.firefighter.aenitto.auth.service;

import org.springframework.stereotype.Service;

import com.firefighter.aenitto.auth.dto.request.LoginRequestV2;
import com.firefighter.aenitto.auth.dto.response.LoginResponse;

// TODO: AuthService하나로 합치기
@Service
public interface AuthServiceV2 {
	LoginResponse loginOrSignInV2(LoginRequestV2 loginRequestV2);
}
