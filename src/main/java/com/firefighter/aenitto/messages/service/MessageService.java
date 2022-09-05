package com.firefighter.aenitto.messages.service;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.messages.dto.request.SendMessageRequest;
import com.firefighter.aenitto.messages.dto.response.MemoriesResponse;
import com.firefighter.aenitto.messages.dto.response.ReceivedMessagesResponse;
import com.firefighter.aenitto.messages.dto.response.SentMessagesResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {
    long sendMessage(Member currentMember, Long roomId, SendMessageRequest request, MultipartFile image);
    long sendMessageSeparate(Member currentMember, Long roomId, String manitteeId
                             ,String messageContent, MultipartFile image);
    SentMessagesResponse getSentMessages(Member currentMember, Long roomId);
    void setReadMessagesStatus(Member currentMember, Long roomId);
    ReceivedMessagesResponse getReceivedMessages(Member currentMember, Long roomId);
    MemoriesResponse getMemories(Member currentMember, Long roomId);
}
