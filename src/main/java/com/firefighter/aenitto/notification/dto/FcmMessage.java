package com.firefighter.aenitto.notification.dto;

import com.firefighter.aenitto.messages.domain.Message;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(force = true)
@Getter
public class FcmMessage {
    private boolean validateOnly;
    private Message message;

    @Builder
    public FcmMessage(String targetToken, String title, String body, String link, Long roomId) {
        this.message = Message.builder()
                .token(targetToken)
                .notification(Notification.builder()
                        .title(title).body(body).build())
                .data(Data.builder().title(title).body(body).roomId(roomId.toString()).link(link).build())
                .build();
    }

    @Builder
    @Getter
    private static class Message {
        private final String token;
        private final Notification notification;
        private final Data data;
    }

    @Builder
    @Getter
    private static class Notification {
        private final String title;
        private final String body;
    }

    @Builder
    @Getter
    private static class Data {
        private final String title;
        private final String body;
        private final String link;
        private final String roomId;
    }
}
