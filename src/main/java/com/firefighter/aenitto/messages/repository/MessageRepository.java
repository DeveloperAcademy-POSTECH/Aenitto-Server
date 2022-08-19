package com.firefighter.aenitto.messages.repository;

import java.util.UUID;

public interface MessageRepository {
    public int findUnreadMessageCount(UUID memberId, Long roomId);
}
