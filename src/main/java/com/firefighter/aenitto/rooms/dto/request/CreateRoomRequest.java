package com.firefighter.aenitto.rooms.dto.request;

import com.firefighter.aenitto.common.annotation.validation.CustomDate;
import com.firefighter.aenitto.common.utils.DateConverter;
import com.firefighter.aenitto.rooms.domain.Room;
import jakarta.validation.GroupSequence;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(force = true)
public class CreateRoomRequest {

  @Valid
  private final RoomRequest room;
  @Valid
  private final MemberRequest member;

  public Room toEntity() {
    return Room.builder()
        .title(room.title)
        .capacity(room.capacity)
        .startDate(DateConverter.stringToLocalDate(room.startDate))
        .endDate(DateConverter.stringToLocalDate(room.endDate))
        .build();
  }

  @Builder
  public CreateRoomRequest(String title, int capacity, String startDate, String endDate, int colorIdx) {
    this.room = RoomRequest.builder()
        .capacity(capacity)
        .startDate(startDate)
        .endDate(endDate)
        .title(title)
        .build();
    this.member = new MemberRequest(colorIdx);
  }

  @Getter
  @NoArgsConstructor(force = true)
  @GroupSequence({RoomRequest.class, RoomRequest.SubOrderedConstraints.class})
  public static class RoomRequest {
    @NotNull
    @Size(min = 1, max = 8)
    private final String title;

    @NotNull
    @Min(4)
    @Max(15)
    private final int capacity;
    @NotNull
    @CustomDate(groups = SubOrderedConstraints.class)
    private final String startDate;

    @NotNull
    @CustomDate(groups = SubOrderedConstraints.class)
    private final String endDate;

    @Builder
    private RoomRequest(String title, int capacity, String startDate, String endDate) {
      this.title = title;
      this.capacity = capacity;
      this.startDate = startDate;
      this.endDate = endDate;
    }

    private interface SubOrderedConstraints {
    }
  }

  @Getter
  @NoArgsConstructor(force = true)
  @AllArgsConstructor
  public static class MemberRequest {
    private final int colorIdx;
  }
}