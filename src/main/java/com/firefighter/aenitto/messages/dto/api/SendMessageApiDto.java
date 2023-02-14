package com.firefighter.aenitto.messages.dto.api;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SendMessageApiDto {
	private final Long roomId;
	private final String manitteeId;
	private final String messageContent;
	private final MultipartFile image;
	private final Long missionId;

	public UUID getManitteeId() {
		return UUID.fromString(this.manitteeId);
	}


	public boolean isImageNotNull() {
		return image != null;
	}
}