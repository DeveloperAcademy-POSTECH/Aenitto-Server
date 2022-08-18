package com.firefighter.aenitto.rooms.dto.request;

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

    @Min(5) @Max(15)
    private final Integer capacity;
    private final String startDate;
    private final String endDate;
}
