package com.firefighter.aenitto.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class ReissueTokenResponse {
    private final String accessToken;
    private final String refreshToken;

    @Builder
    public ReissueTokenResponse(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
