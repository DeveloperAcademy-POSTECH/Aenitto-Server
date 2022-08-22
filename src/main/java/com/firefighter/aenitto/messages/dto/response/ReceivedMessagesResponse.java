package com.firefighter.aenitto.messages.dto.response;

import com.firefighter.aenitto.messages.domain.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class ReceivedMessagesResponse {
    private final int count;
    private final List<MessageResponse> messages;

    public static ReceivedMessagesResponse of(List<Message> messages) {
        return ReceivedMessagesResponse.builder()
                .count(messages.size())
                .messages(MessageResponse.listOf(messages)).build();
    }
}
