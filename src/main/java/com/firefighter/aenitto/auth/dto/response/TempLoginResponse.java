package com.firefighter.aenitto.auth.dto.response;

import com.firefighter.aenitto.auth.token.Token;
import com.firefighter.aenitto.common.utils.DateConverter;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.dto.response.VerifyInvitationResponse;
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
