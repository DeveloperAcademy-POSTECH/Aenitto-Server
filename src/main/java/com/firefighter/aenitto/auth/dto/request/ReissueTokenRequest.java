package com.firefighter.aenitto.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class ReissueTokenRequest {
    private final String accessToken;
    private final String refreshToken;

    @Builder
    public ReissueTokenRequest(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
