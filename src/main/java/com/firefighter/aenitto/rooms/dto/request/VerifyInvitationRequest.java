package com.firefighter.aenitto.rooms.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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