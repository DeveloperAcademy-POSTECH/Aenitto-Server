package com.firefighter.aenitto.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefighter.aenitto.auth.dto.request.ReissueTokenRequest;
import com.firefighter.aenitto.auth.dto.request.TempLoginRequest;
import com.firefighter.aenitto.auth.dto.response.ReissueTokenResponse;
import com.firefighter.aenitto.auth.dto.response.TempLoginResponse;
import com.firefighter.aenitto.auth.service.AuthService;
import com.firefighter.aenitto.common.exception.GlobalExceptionHandler;
import com.firefighter.aenitto.common.exception.auth.AuthErrorCode;
import com.firefighter.aenitto.common.exception.auth.InvalidTokenException;
import com.firefighter.aenitto.common.exception.room.RoomErrorCode;
import com.firefighter.aenitto.common.exception.room.RoomNotParticipatingException;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.messages.dto.response.ReceivedMessagesResponse;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
                .setControllerAdvice(GlobalExceptionHandler.class)
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

    @DisplayName("토큰 재발급 / 실패 - 유효한 토큰이 아님")
    @Test
    void reissueToken_fail_not_valid_token() throws Exception {
        // given
        final String url = "/api/v1/auth/reissue";
        ReissueTokenRequest request = ReissueTokenRequest.builder()
                .accessToken("accessToken").refreshToken("refreshToken").build();
        doThrow(new InvalidTokenException()).when(authService)
                .reissueAccessToken(any(ReissueTokenRequest.class));

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.patch(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        perform
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(AuthErrorCode.INVALID_TOKEN.getStatus().value())))
                .andExpect(jsonPath("$.message", is(AuthErrorCode.INVALID_TOKEN.getMessage())));
    }

    @DisplayName("토큰 재발급 / 성공")
    @Test
    void reissueToken_success() throws Exception {
        // given
        final String url = "/api/v1/auth/reissue";
        ReissueTokenRequest request = ReissueTokenRequest.builder()
                .accessToken("accessToken").refreshToken("refreshToken").build();
        doReturn(ReissueTokenResponse.builder()
                        .accessToken("accessToken").refreshToken("refreshToken").build())
                .when(authService).reissueAccessToken(any(ReissueTokenRequest.class));

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.patch(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        perform
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("토큰 재발급",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("accessToken").description("ACCESS TOKEN"),
                                fieldWithPath("refreshToken").description("재발급 토큰")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("ACCESS TOKEN"),
                                fieldWithPath("refreshToken").description("재발급 토큰")
                        )
                ));
        ;
    }
}
