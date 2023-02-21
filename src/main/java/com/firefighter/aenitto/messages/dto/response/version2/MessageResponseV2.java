package com.firefighter.aenitto.messages.dto.response.version2;

import java.util.List;
import java.util.stream.Collectors;

import com.firefighter.aenitto.common.utils.DateConverter;
import com.firefighter.aenitto.messages.domain.Message;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.rooms.dto.response.RoomDetailResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class MessageResponseV2 {
	private final Long id;
	private final String content;
	private final String imageUrl;
	private final String createdDate;
	private final MissionInfo missionInfo;

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(force = true)
	public static class MissionInfo {
		private final Long id;
		private final String content;

		public static MissionInfo setMissionId(Long missionId) {
			return MissionInfo.builder()
				.id(missionId)
				.build();
		}

		public static MissionInfo setMissionContent(Mission mission) {
			return MissionInfo.builder()
				.content(mission.getContent())
				.build();
		}
	}

	public static MessageResponseV2 of(Message message) {
		return MessageResponseV2.builder().id(message.getId())
			.content(message.getContent())
			.createdDate(DateConverter.localDateToString(message.getCreatedAt().toLocalDate()))
			.missionInfo(MissionInfo.setMissionId(message.getMissionId()))
			.imageUrl(message.getImgUrl()).build();
	}

	public static List<MessageResponseV2> listOf(List<Message> messages) {
		return messages.stream().map(MessageResponseV2::of)
			.collect(Collectors.toList());
	}

	public boolean hasMission() {
		if (missionInfo.id != null) {
			System.out.println(missionInfo.id);
			return true;
		} else {
			return false;
		}
	}
}
