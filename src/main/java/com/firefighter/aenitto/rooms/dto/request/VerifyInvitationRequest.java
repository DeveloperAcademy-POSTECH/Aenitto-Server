package com.firefighter.aenitto.rooms.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class VerifyInvitationRequest {
  @NotNull
  @Size(min = 6, max = 6)
  private final String invitationCode;

  @Builder
  public VerifyInvitationRequest(String invitationCode) {
    this.invitationCode = invitationCode;
  }
}