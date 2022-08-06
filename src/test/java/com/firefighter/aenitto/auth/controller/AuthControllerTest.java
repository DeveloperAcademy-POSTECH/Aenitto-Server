package com.firefighter.aenitto.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefighter.aenitto.auth.dto.request.TempLoginRequest;
import com.firefighter.aenitto.auth.dto.response.TempLoginResponse;
import com.firefighter.aenitto.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import static org.hamcrest.Matchers.is;

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
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class AuthControllerTest {

    @InjectMocks
    private AuthController target;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock @Qualifier("authServiceImpl")
    private AuthService authService;

    @BeforeEach
    void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("임시 사용자 생성 성공")
    public void 임시사용자생성_성공() throws Exception{
        //given
        final String url = "/api/v1/temp-login";
        final TempLoginResponse tempLoginResponse = TempLoginResponse.builder()
                .accessToken("accessToken").build();

        doReturn(tempLoginResponse).when(authService).loginOrSignIn(any(TempLoginRequest.class));

        //when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(
                                TempLoginRequest.builder()
                                        .accessToken("accessToken")
                                        .build())
                        ).contentType(MediaType.APPLICATION_JSON)
        );

        // then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", is(tempLoginResponse.getAccessToken())))
                .andDo(document("temp-login",
                        preprocessRequest(prettyPrint()),   // (2)
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("accessToken").description("토큰")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("발급된 토큰")
                        )
                )
        );
    }

}
