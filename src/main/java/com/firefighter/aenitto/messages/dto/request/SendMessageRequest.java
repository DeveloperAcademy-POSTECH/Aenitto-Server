package com.firefighter.aenitto.messages.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Builder
public class SendMessageRequest {

    private String recieverId;

    private String messageContent;
}
