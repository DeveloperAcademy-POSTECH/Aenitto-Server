package com.firefighter.aenitto.rooms.dto.request;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.firefighter.aenitto.rooms.domain.MemberRoom;

@Getter
@NoArgsConstructor(force = true)
public class ParticipateRoomRequest {
	@NotNull
	private final int colorIdx;

	@Builder
	public ParticipateRoomRequest(int colorIdx) {
		this.colorIdx = colorIdx;
	}

	public MemberRoom toEntity() {
		return MemberRoom.builder()
			.admin(false)
			.colorIdx(colorIdx)
			.build();
	}
}
