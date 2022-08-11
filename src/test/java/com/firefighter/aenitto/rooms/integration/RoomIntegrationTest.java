package com.firefighter.aenitto.rooms.integration;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.dto.request.ChangeNicknameRequest;
import com.firefighter.aenitto.rooms.dto.RoomRequestDtoBuilder;
import com.firefighter.aenitto.rooms.dto.request.CreateRoomRequest;
import com.firefighter.aenitto.support.IntegrationTest;
import com.firefighter.aenitto.support.security.WithMockCustomMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockCustomMember
public class RoomIntegrationTest extends IntegrationTest {

    @DisplayName("방 생성 -> 성공")
    @Test
    @WithMockCustomMember
    void create_room() throws Exception {
        // given
        CreateRoomRequest request = RoomRequestDtoBuilder.createRoomRequest();

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/rooms")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

}
