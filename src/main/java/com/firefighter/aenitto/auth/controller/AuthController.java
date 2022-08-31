package com.firefighter.aenitto.auth.controller;

import com.firefighter.aenitto.auth.dto.request.ReissueTokenRequest;
import com.firefighter.aenitto.auth.dto.request.TempLoginRequest;
import com.firefighter.aenitto.auth.dto.response.ReissueTokenResponse;
import com.firefighter.aenitto.auth.dto.response.TempLoginResponse;
import com.firefighter.aenitto.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    @Qualifier("authServiceImpl")
    private final AuthService authService;

    @PostMapping("/temp-login")
    public ResponseEntity temporaryLogin(
            @Valid @RequestBody final TempLoginRequest tempLoginRequest
            ) {
        final TempLoginResponse response = authService.loginOrSignIn(tempLoginRequest);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/auth/reissue")
    public ResponseEntity<ReissueTokenResponse> reissueAccessToken(
            @Valid @RequestBody final ReissueTokenRequest reissueTokenRequest
            ) {
        return ResponseEntity.ok(authService.reissueAccessToken(reissueTokenRequest));
    }
}
