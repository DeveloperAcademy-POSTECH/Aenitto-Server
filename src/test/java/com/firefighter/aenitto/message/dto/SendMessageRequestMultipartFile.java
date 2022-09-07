package com.firefighter.aenitto.message.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefighter.aenitto.messages.dto.request.SendMessageRequest;
import org.springframework.mock.web.MockMultipartFile;

public class SendMessageRequestMultipartFile {
    private static ObjectMapper objectMapper;
    public static MockMultipartFile requestMultipartFile() throws JsonProcessingException {
        objectMapper = new ObjectMapper();

        SendMessageRequest request = SendMessageRequest.builder()
                .manitteeId("b383cdb3-a871-4410-b147-fb1f7b447b9e").messageContent("adslfajf").build();
        String requestJson = objectMapper.writeValueAsString(request);
        MockMultipartFile requestMultipartfile = new MockMultipartFile("testMessageRequest", "testMessageRequest", "application/json", requestJson.getBytes());
        return requestMultipartfile;
    }
}
