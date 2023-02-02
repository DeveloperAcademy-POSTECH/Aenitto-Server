package com.firefighter.aenitto.auth.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import com.firefighter.aenitto.auth.dto.request.ReissueTokenRequest;
import com.firefighter.aenitto.auth.dto.response.ReissueTokenResponse;
import com.firefighter.aenitto.auth.dto.request.LoginRequest;
import com.firefighter.aenitto.auth.dto.response.LoginResponse;
import com.firefighter.aenitto.auth.service.AuthService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

	@Qualifier("authServiceImpl")
	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> loginAndSignIn(@Valid @RequestBody final LoginRequest loginRequest) {
		return ResponseEntity.ok(authService.loginOrSignIn(loginRequest));
	}

	@PatchMapping("/auth/reissue")
	public ResponseEntity<ReissueTokenResponse> reissueAccessToken(
		@Valid @RequestBody final ReissueTokenRequest reissueTokenRequest) {
		return ResponseEntity.ok(authService.reissueAccessToken(reissueTokenRequest));
	}
}
