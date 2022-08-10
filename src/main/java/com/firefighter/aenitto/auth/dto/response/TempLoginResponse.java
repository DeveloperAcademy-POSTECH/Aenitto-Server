package com.firefighter.aenitto.auth.dto.response;

import com.firefighter.aenitto.auth.token.Token;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class TempLoginResponse {

    private final String accessToken;

    public static TempLoginResponse from(Token token) {
        return TempLoginResponse.builder()
                .accessToken(token.getAccessToken())
                .build();
    }
}
