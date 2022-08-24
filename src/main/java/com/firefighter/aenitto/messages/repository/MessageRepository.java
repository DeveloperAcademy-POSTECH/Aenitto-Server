package com.firefighter.aenitto.messages.repository;

import com.firefighter.aenitto.messages.domain.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message saveMessage(Message message);
    int findUnreadMessageCount(UUID memberId, Long roomId);
    List<Message> getSentMessages (UUID memberId, Long roomId);
    List<Message> findMessagesByReceiverIdAndRoomIdAndStatus(UUID receiverId, Long roomId, boolean status);
    List<Message> getReceivedMessages (UUID receiverId, Long roomId);
}
