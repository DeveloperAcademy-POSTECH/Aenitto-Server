package com.firefighter.aenitto.auth.controller;

import com.firefighter.aenitto.auth.dto.request.TempLoginRequest;
import com.firefighter.aenitto.auth.dto.response.TempLoginResponse;
import com.firefighter.aenitto.auth.service.AuthService;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.rooms.dto.request.VerifyInvitationRequest;
import com.firefighter.aenitto.rooms.dto.response.VerifyInvitationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/temp-login")
    public ResponseEntity temporaryLogin(
            @Valid @RequestBody final TempLoginRequest tempLoginRequest
            ) {
        final TempLoginResponse response = authService.loginOrSignIn(tempLoginRequest);
        return ResponseEntity.ok(response);
    }
}
