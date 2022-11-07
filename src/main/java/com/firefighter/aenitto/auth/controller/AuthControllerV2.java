package com.firefighter.aenitto.auth.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firefighter.aenitto.auth.dto.request.LoginRequest;
import com.firefighter.aenitto.auth.dto.request.LoginRequestV2;
import com.firefighter.aenitto.auth.dto.response.LoginResponse;
import com.firefighter.aenitto.auth.service.AuthService;
import com.firefighter.aenitto.auth.service.AuthServiceV2;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class AuthControllerV2 {
	@Qualifier("authServiceImplV2")
	private final AuthServiceV2 authService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> loginAndSignIn(
		@Valid @RequestBody final LoginRequestV2 loginRequest
	) {
		return ResponseEntity.ok(authService.loginOrSignInV2(loginRequest));
	}
}
