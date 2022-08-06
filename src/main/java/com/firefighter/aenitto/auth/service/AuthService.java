package com.firefighter.aenitto.auth.service;

import com.firefighter.aenitto.auth.domain.RefreshToken;
import com.firefighter.aenitto.auth.dto.request.TempLoginRequest;
import com.firefighter.aenitto.auth.dto.response.TempLoginResponse;
import com.firefighter.aenitto.members.domain.Member;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface AuthService {
    TempLoginResponse loginOrSignIn(TempLoginRequest tempLoginRequest);
}
