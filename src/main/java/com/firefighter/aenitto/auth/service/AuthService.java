package com.firefighter.aenitto.auth.service;


import com.firefighter.aenitto.auth.dto.request.LoginRequestV2;
import com.firefighter.aenitto.auth.dto.request.ReissueTokenRequest;
import com.firefighter.aenitto.auth.dto.response.ReissueTokenResponse;

import com.firefighter.aenitto.auth.dto.request.LoginRequest;
import com.firefighter.aenitto.auth.dto.response.LoginResponse;

import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    ReissueTokenResponse reissueAccessToken(ReissueTokenRequest reissueTokenRequest);
    LoginResponse loginOrSignIn(LoginRequest tempLoginRequest);
}
