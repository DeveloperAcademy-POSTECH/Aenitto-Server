package com.firefighter.aenitto.rooms.dto.request;


import com.firefighter.aenitto.rooms.domain.MemberRoom;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class ParticipateRoomRequest {
  @NotNull
  private final int colorIdx;

  @Builder
  public ParticipateRoomRequest(int colorIdx) {
    this.colorIdx = colorIdx;
  }

  public MemberRoom toEntity() {
    return MemberRoom.builder()
        .admin(false)
        .colorIdx(colorIdx)
        .build();
  }
}
