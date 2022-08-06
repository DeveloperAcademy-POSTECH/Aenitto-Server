package com.firefighter.aenitto.auth.service;

import com.firefighter.aenitto.auth.dto.request.TempLoginRequest;
import com.firefighter.aenitto.auth.dto.response.TempLoginResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    TempLoginResponse loginOrSignIn(TempLoginRequest tempLoginRequest);
}
