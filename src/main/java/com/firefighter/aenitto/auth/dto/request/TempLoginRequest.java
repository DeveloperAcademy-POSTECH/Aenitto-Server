package com.firefighter.aenitto.auth.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class TempLoginRequest {
    private final String accessToken;
}
