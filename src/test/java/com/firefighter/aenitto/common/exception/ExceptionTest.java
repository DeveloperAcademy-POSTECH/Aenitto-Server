package com.firefighter.aenitto.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefighter.aenitto.common.exception.test.ExceptionTestController;

import com.firefighter.aenitto.common.exception.test.RequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(value = MockitoExtension.class)
public class ExceptionTest {
    @InjectMocks
    private ExceptionTestController controller;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
        objectMapper = new ObjectMapper();
    }

    @DisplayName("CustomException Handling 테스트")
    @Test
    void customExceptionTest() throws Exception {
        // given
        final String url = "/test/custom-exception";

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.message", "테스트 에러 메시지 입니다").exists())
                .andExpect(jsonPath("$.status", 400).exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists())
                .andDo(print());
    }

    @DisplayName("BindException Handling 테스트")
    @Test
    void bindExceptionTest() throws Exception {
        // given
        final String url = "/test/bind-exception";

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(
                                objectMapper.writeValueAsString(new RequestDto(null, 120)))
        );

        perform
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", 400).exists())
                .andExpect(jsonPath("$.message", "입력 조건에 대한 예외입니다").exists())
                .andExpect(jsonPath("$.errors[0].field", "age").exists())
                .andExpect(jsonPath("$.errors[1].field", "name").exists());


    }

}
