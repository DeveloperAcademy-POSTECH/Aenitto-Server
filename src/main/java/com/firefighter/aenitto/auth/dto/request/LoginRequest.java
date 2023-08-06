package com.firefighter.aenitto.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class LoginRequest {
  //TODO: pattern 확인 추가하기
  @NotBlank(message = "identityToken은 null일 수 없습니다.")
  //    @Pattern(regexp = "^[0-9a-zA-Z]{1,31}\\.[0-9a-zA-Z]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$")
  private final String identityToken;
}
