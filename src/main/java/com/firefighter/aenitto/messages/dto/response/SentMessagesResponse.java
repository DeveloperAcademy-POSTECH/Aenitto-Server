package com.firefighter.aenitto.messages.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.messages.domain.Message;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class SentMessagesResponse {
	private final int count;
	private final List<MessageResponse> messages;
	private final ManitteeInfoResponse manittee;

	public static SentMessagesResponse of(List<Message> messages, Member manittee) {
		return SentMessagesResponse.builder()
			.count(messages.size())
			.manittee(ManitteeInfoResponse.of(manittee))
			.messages(MessageResponse.listOf(messages)).build();
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(force = true)
	public static class ManitteeInfoResponse {
		private final String id;
		private final String nickname;

		public static ManitteeInfoResponse of(Member member) {
			return ManitteeInfoResponse.builder().id(member.getId().toString())
				.nickname(member.getNickname()).build();
		}
	}
}
