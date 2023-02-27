package com.firefighter.aenitto.messages.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firefighter.aenitto.common.annotation.CurrentMember;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.messages.dto.response.version2.ReceivedMessagesResponseV2;
import com.firefighter.aenitto.messages.dto.response.version2.SentMessagesResponseV2;
import com.firefighter.aenitto.messages.service.MessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/rooms/{roomId}")
@RequiredArgsConstructor
public class MessageControllerV2 {
	@Qualifier("messageServiceImpl")
	private final MessageService messageService;

	@GetMapping("/messages-sent")
	public ResponseEntity<SentMessagesResponseV2> getSentMessagesV2(
		@CurrentMember Member currentMember,
		@PathVariable final Long roomId
	) {
		return ResponseEntity.ok(messageService.getSentMessagesV2(currentMember, roomId));
	}

	@GetMapping("/messages-received")
	public ResponseEntity<ReceivedMessagesResponseV2> getReceivedMessagesV2(
		@CurrentMember Member currentMember,
		@PathVariable final Long roomId
	) {
		return ResponseEntity.ok(messageService.getReceivedMessagesV2(currentMember, roomId));
	}
}
