package com.firefighter.aenitto.messages.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.firefighter.aenitto.messages.domain.Message;
import com.firefighter.aenitto.rooms.domain.MemberRoom;

@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor

public class MemoriesResponse {
	private final Memory memoriesWithManitto;
	private final Memory memoriesWithManittee;

	public static MemoriesResponse of(MemberRoom myManitto, MemberRoom myManittee,
		List<Message> memoriesWithManitto, List<Message> memoriesWithManittee) {
		return MemoriesResponse.builder()
			.memoriesWithManittee(Memory.of(myManittee, memoriesWithManittee))
			.memoriesWithManitto(Memory.of(myManitto, memoriesWithManitto))
			.build();
	}

	@Getter
	@Builder
	@AllArgsConstructor
	public static class Memory {
		private final MemberInfo member;
		private final List<MessageResponse> messages;

		public static Memory of(MemberRoom memberRoom, List<Message> messages) {
			return Memory.builder().member(new MemberInfo(memberRoom))
				.messages(MessageResponse.listOf(messages)).build();
		}

		@Getter
		@NoArgsConstructor(force = true)
		public static class MemberInfo {
			private final String nickname;
			private final long colorIdx;

			public MemberInfo(MemberRoom memberRoom) {
				nickname = memberRoom.getMember().getNickname();
				colorIdx = memberRoom.getColorIdx();
			}
		}
	}
}
