package com.firefighter.aenitto.messages.controller;

import com.firefighter.aenitto.common.annotation.CurrentMember;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.messages.dto.api.SendMessageApiDto;
import com.firefighter.aenitto.messages.dto.request.SendMessageRequest;
import com.firefighter.aenitto.messages.dto.response.MemoriesResponse;
import com.firefighter.aenitto.messages.dto.response.ReceivedMessagesResponse;
import com.firefighter.aenitto.messages.dto.response.SentMessagesResponse;
import com.firefighter.aenitto.messages.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/rooms/{roomId}")
@RequiredArgsConstructor
public class MessageController {

  @Qualifier("messageServiceImpl")
  private final MessageService messageService;

  @Deprecated
  @PostMapping("/messages")
  public ResponseEntity createMessage(
      @CurrentMember final Member currentMember,
      @PathVariable final Long roomId,
      @RequestPart @Nullable final MultipartFile image,
      @Valid @RequestPart final SendMessageRequest testMessageRequest
  ) {
    final Long messageId = messageService.sendMessage(currentMember, roomId,
        testMessageRequest, image);
    return ResponseEntity.created(URI.create("/api/v1/rooms/" + roomId + "/messages/" + messageId)).build();
  }

  @PostMapping("/messages-separate")
  public ResponseEntity createMessageSeparate(
      @CurrentMember final Member currentMember,
      @PathVariable final Long roomId,
      @RequestPart @Nullable final MultipartFile image,
      @Valid @RequestPart final String manitteeId,
      @Nullable @RequestPart() final String messageContent,
      @Nullable @RequestPart(required = false) final String missionId
  ) {
    final Long messageId = messageService.sendMessageSeparate(currentMember,
        new SendMessageApiDto(roomId, manitteeId, messageContent, image, missionId));
    return ResponseEntity.created(URI.create("/api/v1/rooms/" + roomId + "/messages/" + messageId)).build();
  }

  @GetMapping("/messages-sent")
  public ResponseEntity<SentMessagesResponse> getSentMessages(
      @CurrentMember Member currentMember,
      @PathVariable final Long roomId
  ) {
    return ResponseEntity.ok(messageService.getSentMessages(currentMember, roomId));
  }

  @PatchMapping("/messages/status")
  public ResponseEntity setReadMessagesStatus(
      @CurrentMember final Member currentMember,
      @PathVariable final Long roomId
  ) {
    messageService.setReadMessagesStatus(currentMember, roomId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/messages-received")
  public ResponseEntity<ReceivedMessagesResponse> getReceivedMessages(
      @CurrentMember Member currentMember,
      @PathVariable final Long roomId
  ) {
    return ResponseEntity.ok(messageService.getReceivedMessages(currentMember, roomId));
  }

  @GetMapping("/memories")
  public ResponseEntity<MemoriesResponse> getMemories(
      @CurrentMember Member currentMember,
      @PathVariable final Long roomId
  ) {
    return ResponseEntity.ok(messageService.getMemories(currentMember, roomId));
  }
}
