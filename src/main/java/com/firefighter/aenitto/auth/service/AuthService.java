package com.firefighter.aenitto.auth.service;

import com.firefighter.aenitto.auth.dto.request.LoginRequest;
import com.firefighter.aenitto.auth.dto.response.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    LoginResponse loginOrSignIn(LoginRequest tempLoginRequest);
}
