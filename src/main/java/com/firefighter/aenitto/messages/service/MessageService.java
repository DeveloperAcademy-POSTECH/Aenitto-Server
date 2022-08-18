package com.firefighter.aenitto.messages.service;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.messages.dto.request.SendMessageRequest;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {
    public long sendMessage(Member currentMember, Long roomId, SendMessageRequest request, MultipartFile image);
}
