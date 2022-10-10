package com.firefighter.aenitto.auth.dto.request;

import com.firefighter.aenitto.common.annotation.ValidUUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Builder
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class LoginRequest {
    //TODO: pattern 확인 추가하기
    @NotBlank(message = "identityToken은 null일 수 없습니다.")
//    @Pattern(regexp = "^[0-9a-zA-Z]{1,31}\\.[0-9a-zA-Z]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$")
    private final String identityToken;

    @NotBlank
    private final String fcmToken;
}
