package com.firefighter.aenitto.auth.controller;

import com.firefighter.aenitto.auth.dto.request.LoginRequest;
import com.firefighter.aenitto.auth.dto.response.LoginResponse;
import com.firefighter.aenitto.auth.service.AuthService;
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
            @Valid @RequestBody final LoginRequest tempLoginRequest
            ) {
        final LoginResponse response = authService.loginOrSignIn(tempLoginRequest);
        return ResponseEntity.ok(response);
    }
}
