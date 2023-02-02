package com.firefighter.aenitto.messages.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.firefighter.aenitto.common.utils.DateConverter;
import com.firefighter.aenitto.messages.domain.Message;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class MessageResponse {
	private final Long id;
	private final String content;
	private final String imageUrl;
	private final String createdDate;

	public static MessageResponse of(Message message) {
		return MessageResponse.builder().id(message.getId())
			.content(message.getContent())
			.createdDate(DateConverter.localDateToString(message.getCreatedAt().toLocalDate()))
			.imageUrl(message.getImgUrl()).build();
	}

	public static List<MessageResponse> listOf(List<Message> messages) {
		return messages.stream().map(MessageResponse::of)
			.collect(Collectors.toList());
	}
}
