package com.firefighter.aenitto.rooms.dto.response;

import com.firefighter.aenitto.common.utils.DateConverter;
import com.firefighter.aenitto.rooms.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class VerifyInvitationResponse {
    private final Long id;
    private final String title;
    private final int capacity;
    private final int participatingCount;
    private final String startDate;
    private final String endDate;

    public static VerifyInvitationResponse from(Room room) {
        return VerifyInvitationResponse.builder()
                .id(room.getId())
                .title(room.getTitle())
                .capacity(room.getCapacity())
                .startDate(DateConverter.localDateToString(room.getStartDate()))
                .endDate(DateConverter.localDateToString(room.getEndDate()))
                .participatingCount(room.getMemberRooms().size())
                .build();
    }
}
