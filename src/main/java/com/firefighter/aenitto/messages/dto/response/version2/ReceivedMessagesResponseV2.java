package com.firefighter.aenitto.messages.dto.response.version2;

import java.util.List;

import com.firefighter.aenitto.messages.domain.Message;
import com.firefighter.aenitto.messages.dto.response.MessageResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class ReceivedMessagesResponseV2 {
	private final int count;
	private final List<MessageResponseV2> messages;

	public static ReceivedMessagesResponseV2 of(List<Message> messages) {
		return ReceivedMessagesResponseV2.builder()
			.count(messages.size())
			.messages(MessageResponseV2.listOf(messages)).build();
	}
}
