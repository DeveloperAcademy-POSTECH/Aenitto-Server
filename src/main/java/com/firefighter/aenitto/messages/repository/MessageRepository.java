package com.firefighter.aenitto.messages.repository;

import com.firefighter.aenitto.messages.domain.Message;

import java.util.UUID;

public interface MessageRepository {
    public Message saveMessage(Message message);
    public int findUnreadMessageCount(UUID memberId, Long roomId);
}
