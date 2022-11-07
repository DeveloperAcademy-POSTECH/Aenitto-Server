package com.firefighter.aenitto.rooms.dto.request;

import com.firefighter.aenitto.common.annotation.validation.CustomDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UpdateRoomRequest {
    @Size(min = 1, max = 8)
    private final String title;

    @Min(4) @Max(15)
    private final Integer capacity;

    @CustomDate
    private final String startDate;

    @CustomDate
    private final String endDate;
}
