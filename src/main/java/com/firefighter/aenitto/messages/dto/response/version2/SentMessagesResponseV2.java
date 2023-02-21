package com.firefighter.aenitto.messages.dto.response.version2;

import java.util.List;

import com.firefighter.aenitto.members.domain.Member;
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
public class SentMessagesResponseV2 {
	private final int count;
	private final List<MessageResponseV2> messages;
	private final ManitteeInfoResponse manittee;

	public static SentMessagesResponseV2 of(List<Message> messages, Member manittee) {
		return SentMessagesResponseV2.builder()
			.count(messages.size())
			.manittee(ManitteeInfoResponse.of(manittee))
			.messages(MessageResponseV2.listOf(messages)).build();
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
