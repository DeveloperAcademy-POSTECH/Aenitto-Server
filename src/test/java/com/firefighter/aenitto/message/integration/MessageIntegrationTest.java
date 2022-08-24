package com.firefighter.aenitto.message.integration;

import com.firefighter.aenitto.common.exception.mission.MissionErrorCode;
import com.firefighter.aenitto.common.exception.room.RoomErrorCode;
import com.firefighter.aenitto.common.utils.SqlPath;
import com.firefighter.aenitto.messages.dto.request.SendMessageRequest;
import com.firefighter.aenitto.rooms.dto.RoomRequestDtoBuilder;
import com.firefighter.aenitto.support.IntegrationTest;
import com.firefighter.aenitto.support.security.WithMockCustomMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.firefighter.aenitto.message.ImageFixture.IMAGE;
import static com.firefighter.aenitto.message.dto.SendMessageRequestMultipartFile.requestMultipartFile;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WithMockCustomMember
public class MessageIntegrationTest extends IntegrationTest {

    @DisplayName("메시지 보내기 -> 성공")
    @Sql("classpath:relation.sql")
    @Test
    void send_message() throws Exception {
        // given
        MockMultipartFile image = IMAGE;

        //TODO createJson 사용해서 바꾸기 - 다온

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/rooms/{roomId}/messages", 1L)
                        .file(image)
                        .file(requestMultipartFile())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @DisplayName("보낸 메시지 가져오기 - 실패 / 참여하고 있는 방이 아님")
    @Sql("classpath:relation.sql")
    @Test
    void get_sent_messages_failure_room_not_participating() throws Exception {
        // when, then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/rooms/{roomId}/messages-sent", 1L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is(RoomErrorCode.ROOM_NOT_PARTICIPATING.getMessage())));
    }

    @DisplayName("보낸 메시지 가져오기 - 실패 / 마니또 존재 X")
    @Sql({
            SqlPath.MEMBER,
            SqlPath.ROOM_PROCESSING,
            SqlPath.MEMBER_ROOM
    })
    @Test
    void get_sent_messages_failure_manittee_not_exists() throws Exception {
        // when, then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/rooms/{roomId}/messages-sent", 2L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(RoomErrorCode.RELATION_NOT_FOUND.getMessage())));
    }

    @DisplayName("보낸 메시지 가져오기 - 성공")
    @Sql({
            SqlPath.MEMBER,
            SqlPath.ROOM_PROCESSING,
            SqlPath.MEMBER_ROOM,
            SqlPath.RELATION,
            SqlPath.SENT_MESSAGE
    })
    @Test
    void get_sent_messages_success() throws Exception {
        // when, then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/rooms/{roomId}/messages-sent", 2L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").exists())
                .andExpect(jsonPath("$.messages[0].id").exists())
                .andExpect(jsonPath("$.messages[0].content").exists())
                .andExpect(jsonPath("$.messages[0].imageUrl").exists())
        ;
    }

    @DisplayName("메세지 읽음으로 상태변경 - 실패 / 참여하고 있는 방이 아님")
    @Test
    void setStatusMessagesRead_failure_room_not_participating() throws Exception {
        // when, then
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/v1/rooms/{roomId}/messages/status", 1L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is(RoomErrorCode.ROOM_NOT_PARTICIPATING.getMessage())));
    }

    @DisplayName("메세지 읽음으로 상태변경 - 성공")
    @Sql({
            SqlPath.MEMBER,
            SqlPath.ROOM_PROCESSING,
            SqlPath.MEMBER_ROOM,
            SqlPath.RELATION,
            SqlPath.SENT_MESSAGE
    })
    @Test
    void setStatusMessagesRead_success() throws Exception {
        // when, then
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/v1/rooms/{roomId}/messages/status", 2L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
    @DisplayName("받은 메시지 가져오기 - 실패 / 참여중인 방이 아님")
    @Test
    void get_received_messages_failure_room_not_participating() throws Exception {
        // when, then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/rooms/{roomId}/messages-received", 1L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is(RoomErrorCode.ROOM_NOT_PARTICIPATING.getMessage())));
    }

    @DisplayName("받은 메시지 가져오기 - 실패 / 마니또 존재 X")
    @Sql({
            SqlPath.MEMBER,
            SqlPath.ROOM_PROCESSING,
            SqlPath.MEMBER_ROOM
    })
    @Test
    void get_received_messages_failure_manittee_not_exists() throws Exception {
        // when, then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/rooms/{roomId}/messages-received", 2L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(RoomErrorCode.RELATION_NOT_FOUND.getMessage())));
    }

    @DisplayName("받은 메시지 가져오기 - 성공")
    @Sql({
            SqlPath.MEMBER,
            SqlPath.ROOM_PROCESSING,
            SqlPath.MEMBER_ROOM,
            SqlPath.RELATION,
            SqlPath.RECEIVED_MESSAGE
    })
    @Test
    void get_received_messages_success() throws Exception {
        // when, then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/rooms/{roomId}/messages-received", 2L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").exists())
                .andExpect(jsonPath("$.messages[0].id").exists())
                .andExpect(jsonPath("$.messages[0].content").exists())
                .andExpect(jsonPath("$.messages[0].imageUrl").exists())
        ;
    }
}
