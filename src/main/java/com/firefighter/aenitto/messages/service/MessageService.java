package com.firefighter.aenitto.messages.service;

import org.springframework.web.multipart.MultipartFile;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.messages.dto.api.SendMessageApiDto;
import com.firefighter.aenitto.messages.dto.request.SendMessageRequest;
import com.firefighter.aenitto.messages.dto.response.MemoriesResponse;
import com.firefighter.aenitto.messages.dto.response.ReceivedMessagesResponse;
import com.firefighter.aenitto.messages.dto.response.SentMessagesResponse;
import com.firefighter.aenitto.messages.dto.response.version2.SentMessagesResponseV2;

public interface MessageService {
	long sendMessage(Member currentMember, Long roomId, SendMessageRequest request, MultipartFile image);

	long sendMessageSeparate(Member currentMember, SendMessageApiDto dto);

	SentMessagesResponse getSentMessages(Member currentMember, Long roomId);
	SentMessagesResponseV2 getSentMessagesV2(Member currentMember, Long roomId);

	void setReadMessagesStatus(Member currentMember, Long roomId);

	ReceivedMessagesResponse getReceivedMessages(Member currentMember, Long roomId);

	MemoriesResponse getMemories(Member currentMember, Long roomId);
}
