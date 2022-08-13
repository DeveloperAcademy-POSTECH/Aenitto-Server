package com.firefighter.aenitto.members.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefighter.aenitto.common.exception.GlobalExceptionHandler;
import com.firefighter.aenitto.common.exception.member.MemberErrorCode;
import com.firefighter.aenitto.common.exception.member.MemberNotFoundException;
import com.firefighter.aenitto.members.dto.request.ChangeNicknameRequest;
import com.firefighter.aenitto.members.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class MemberControllerTest {

    @InjectMocks
    private MemberController target;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock @Qualifier("memberServiceImpl")
    private MemberService memberService;

    @BeforeEach
    void init(RestDocumentationContextProvider restDocumentation){
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        objectMapper = new ObjectMapper();
    }
    @DisplayName("닉네임 변경 - 성공")
    @Test
    void set_nickname_success() throws Exception{
        //given
        final String uri = "/api/v1/members/nickname";

        //when
        ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.put(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                .content(objectMapper.writeValueAsString(
                        ChangeNicknameRequest.builder()
                                .nickname("성공")
                                .build())
                ).contentType(MediaType.APPLICATION_JSON));

        //then, docs
        perform.andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("change-nickname",
                        preprocessRequest(prettyPrint()),   // (2)
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickname").description("닉네임")
                        )
                ));
    }

    @DisplayName("닉네임 변경 실패 - 제약조건을 지키지 않은 경우")
    @Test
    void set_nickname_fail_nickname_not_blank() throws Exception {
        //given
        final String uri = "/api/v1/members/nickname";

        // when
        ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.put(uri)
                         .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                        .content(objectMapper.writeValueAsString(
                                ChangeNicknameRequest.builder()
                                        .nickname("다섯글자이상이란말야")
                                        .build())
                        ).contentType(MediaType.APPLICATION_JSON));

         //then, docs
        perform
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", 400).exists())
                .andExpect(jsonPath("$.message", "입력 조건에 대한 예외입니다").exists())
                .andExpect(jsonPath("$.errors[0].field", "nickname").exists());
    }

    @DisplayName("닉네임 변경 실패 - 유저를 찾지 못할 경우")
    @Test
    void set_nickname_fail_not_found_member() throws Exception{
        //given
        final String uri = "/api/v1/members/nickname";
        doThrow(new MemberNotFoundException())
                .when(memberService)
                .setNickname(any(), any());

        //when, then, docs
        mockMvc.perform(
                MockMvcRequestBuilders.put(uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                        .content(objectMapper.writeValueAsString(
                                ChangeNicknameRequest.builder()
                                        .nickname("이건닉넴")
                                        .build())
                        ).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.message", MemberErrorCode.MEMBER_NOT_FOUND.getMessage()).exists())
                        .andExpect(jsonPath("$.status", MemberErrorCode.MEMBER_NOT_FOUND.getStatus()).exists())
                        .andExpect(jsonPath("$.timestamp").exists())
                        .andExpect(jsonPath("$.errors").exists());
    }


}
