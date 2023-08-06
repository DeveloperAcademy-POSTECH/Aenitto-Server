package com.firefighter.aenitto.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class ReissueTokenRequest {
  @NotBlank
  private final String accessToken;
  @NotBlank
  private final String refreshToken;

  @Builder
  public ReissueTokenRequest(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}
