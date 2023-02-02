package com.firefighter.aenitto.messages.repository;

import java.util.List;
import java.util.UUID;

import com.firefighter.aenitto.messages.domain.Message;

public interface MessageRepository {
	Message saveMessage(Message message);

	int findUnreadMessageCount(UUID memberId, Long roomId);

	List<Message> getSentMessages(UUID memberId, Long roomId);

	List<Message> findMessagesByReceiverIdAndRoomIdAndStatus(UUID receiverId, Long roomId, boolean status);

	List<Message> getReceivedMessages(UUID receiverId, Long roomId);

	List<Message> getTwoRandomImageReceivedMessages(UUID receiverId, Long roomId);

	List<Message> getTwoRandomContentReceivedMessages(UUID receiverId, Long roomId);

	List<Message> getTwoRandomContentSentMessages(UUID senderId, Long roomId);

	List<Message> getTwoRandomImageSentMessages(UUID senderId, Long roomId);
}
