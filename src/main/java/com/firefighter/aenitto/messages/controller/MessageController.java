package com.firefighter.aenitto.messages.controller;

import com.firefighter.aenitto.common.annotation.CurrentMember;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.messages.dto.request.SendMessageRequest;
import com.firefighter.aenitto.messages.service.MessageService;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MessageController {

    @Qualifier("messageServiceImpl")
    private final MessageService messageService;

    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity createRoom(
            @CurrentMember final Member currentMember,
            @PathVariable final Long roomId,
            @RequestPart @Nullable final MultipartFile image,
            // TODO : request part에 valid 적용 가능 여부
            @Valid @RequestPart final SendMessageRequest sendMessageRequest
    ) {
        final Long messageId = messageService.sendMessage(currentMember, roomId,
                sendMessageRequest, image);
        return ResponseEntity.created(URI.create("/api/v1/rooms/" + roomId + "/messages/" + messageId)).build();
    }
}