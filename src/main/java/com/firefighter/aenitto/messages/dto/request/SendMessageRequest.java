package com.firefighter.aenitto.messages.dto.request;

import com.firefighter.aenitto.common.annotation.ValidUUID;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor(force = true)
public class SendMessageRequest {

    @NotBlank(message = "마니띠의 id를 보내주세요")
    @ValidUUID
    private String manitteeId;

    @Length(max = 100, message = "메시지는 100자까지 입력 가능합니다.")
    private String messageContent;

    @Builder
    public SendMessageRequest(String manitteeId, String messageContent) {
        this.manitteeId = manitteeId;
        this.messageContent = messageContent;
    }
}
