package com.firefighter.aenitto.auth.controller;

import com.firefighter.aenitto.auth.dto.request.LoginRequest;
import com.firefighter.aenitto.auth.dto.request.ReissueTokenRequest;
import com.firefighter.aenitto.auth.dto.request.WithdrawlRequest;
import com.firefighter.aenitto.auth.dto.response.LoginResponse;
import com.firefighter.aenitto.auth.dto.response.ReissueTokenResponse;
import com.firefighter.aenitto.auth.service.AuthService;
import com.firefighter.aenitto.common.annotation.CurrentMember;
import com.firefighter.aenitto.members.domain.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

  @PostMapping("/withdrawl")
  public ResponseEntity reissueAccessToken(
      @CurrentMember Member member,
      @RequestBody final WithdrawlRequest withdrawlRequest) {
    authService.withdrawlUser(member, withdrawlRequest);
    return ResponseEntity.ok().build();
  }
}
