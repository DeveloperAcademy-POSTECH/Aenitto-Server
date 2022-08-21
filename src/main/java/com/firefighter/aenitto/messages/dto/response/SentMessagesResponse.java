package com.firefighter.aenitto.messages.dto.response;

import com.firefighter.aenitto.messages.domain.Message;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.dto.response.RoomParticipantsResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class SentMessagesResponse {
    private final int count;
    private final List<MessageResponse> messages;

    public static SentMessagesResponse of(List<Message> messages) {
        return SentMessagesResponse.builder()
                .count(messages.size())
                .messages(MessageResponse.listOf(messages)).build();
    }
}
