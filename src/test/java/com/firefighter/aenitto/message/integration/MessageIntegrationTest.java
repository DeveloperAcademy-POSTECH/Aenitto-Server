package com.firefighter.aenitto.message.integration;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
