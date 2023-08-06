package com.firefighter.aenitto.rooms.dto.request;


import com.firefighter.aenitto.common.annotation.validation.CustomDate;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UpdateRoomRequest {
  @Size(min = 1, max = 8)
  private final String title;

  @Min(4)
  @Max(15)
  private final Integer capacity;

  @CustomDate
  private final String startDate;

  @CustomDate
  private final String endDate;
}
