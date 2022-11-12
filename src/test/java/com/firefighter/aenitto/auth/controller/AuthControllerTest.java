package com.firefighter.aenitto.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.firefighter.aenitto.auth.dto.request.LoginRequestV2;
import com.firefighter.aenitto.auth.dto.request.ReissueTokenRequest;
import com.firefighter.aenitto.auth.dto.response.ReissueTokenResponse;

import com.firefighter.aenitto.auth.service.AuthService;
import com.firefighter.aenitto.auth.service.AuthServiceV2;
import com.firefighter.aenitto.common.exception.GlobalExceptionHandler;
import com.firefighter.aenitto.common.exception.auth.AuthErrorCode;
import com.firefighter.aenitto.common.exception.auth.InvalidTokenException;

import com.firefighter.aenitto.auth.dto.request.LoginRequest;
import com.firefighter.aenitto.auth.dto.response.LoginResponse;

import com.firefighter.aenitto.common.exception.auth.FailedToFetchPublicKeyException;
import com.firefighter.aenitto.common.exception.auth.InvalidIdentityTokenException;
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

import static com.firefighter.aenitto.message.dto.SendMessageRequestMultipartFile.requestMultipartFile;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import static org.mockito.Mockito.*;

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

    @Mock
    @Qualifier("authServiceImpl")
    private AuthService authService;

    @Mock
    @Qualifier("authServiceImplV2")
    private AuthServiceV2 authServiceV2;

    @BeforeEach
    void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        objectMapper = new ObjectMapper();
    }

    @DisplayName("회원가입-로그인 / 실패 - identityToken 유효하지 않음")
    @Test
    void signUplogin_fail_Invalid_identityToken() throws Exception {
        //given
        final String uri = "/api/v1/login";

        doThrow(new InvalidIdentityTokenException())
                .when(authService)
                .loginOrSignIn(any());

        //when, then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post(uri)
                                .content(objectMapper.writeValueAsString(
                                        LoginRequest.builder().identityToken("token이요").build()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", AuthErrorCode.INVALID_IDENTITY_TOKEN.getMessage()).exists())
                .andExpect(jsonPath("$.status", AuthErrorCode.INVALID_IDENTITY_TOKEN.getStatus()).exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists());
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

    @DisplayName("회원가입-로그인 / 실패 - apple public key 가져오기 실패")
    @Test
    void signUplogin_fail_apple_public_key() throws Exception {
        //given
        final String uri = "/api/v1/login";

        doThrow(new FailedToFetchPublicKeyException())
                .when(authService)
                .loginOrSignIn(any());

        //when, then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post(uri)
                                .content(objectMapper.writeValueAsString(
                                        LoginRequest.builder().identityToken("token이요").build()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", AuthErrorCode.APPLE_PUBLIC_KEY_FAILURE.getMessage()).exists())
                .andExpect(jsonPath("$.status", AuthErrorCode.APPLE_PUBLIC_KEY_FAILURE.getStatus()).exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists());
    }

    @DisplayName("회원가입-로그인 / 성공")
    @Test
    void signUplogin_success() throws Exception {
        //given
        final String uri = "/api/v1/login";

        LoginRequest request = LoginRequest.builder().identityToken("token이요").build();
        LoginResponse response = LoginResponse.builder().accessToken("accessToken").refreshToken("refreshToken")
                .isNewMember(true).userSettingDone(true).build();
        when(authService.loginOrSignIn(any())).thenReturn(response);

        //when, then, docs
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post(uri)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andDo(document("회원가입 및 로그인",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("identityToken").description("애플의 identityToken")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("토큰"),
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("refreshToken").description("재발급 토큰"),
                                fieldWithPath("isNewMember").description("새로운 멤버인지 기존 멤버인지"),
                                fieldWithPath("userSettingDone").description("닉네임 정보 입력했는지 여부")
                        )
                ));
    }
}
