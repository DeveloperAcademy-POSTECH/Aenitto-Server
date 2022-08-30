package com.firefighter.aenitto.auth.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class LoginRequest {

    @NotBlank(message = "identityToken은 null일 수 없습니다.")
    private final String identityToken;
}
