package com.firefighter.aenitto.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(force = true)
public class ReissueTokenRequest {
    @NotBlank
    private final String accessToken;
    @NotBlank
    private final String refreshToken;

    @Builder
    public ReissueTokenRequest(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
