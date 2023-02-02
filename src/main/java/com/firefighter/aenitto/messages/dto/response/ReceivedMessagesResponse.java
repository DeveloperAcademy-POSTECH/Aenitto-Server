package com.firefighter.aenitto.messages.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.firefighter.aenitto.messages.domain.Message;

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
