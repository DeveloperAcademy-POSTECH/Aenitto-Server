package com.firefighter.aenitto.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefighter.aenitto.auth.dto.request.LoginRequestV2;
import com.firefighter.aenitto.auth.dto.response.LoginResponse;
import com.firefighter.aenitto.auth.service.AuthService;
import com.firefighter.aenitto.auth.service.AuthServiceV2;
import com.firefighter.aenitto.common.exception.GlobalExceptionHandler;


@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class AuthControllerTestV2 {

	@InjectMocks
	private AuthControllerV2 target;

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

	@DisplayName("회원가입-로그인V2 / 성공")
	@Test
	void signUploginV2_success() throws Exception {
		//given
		final String uri = "/api/v2/login";

		LoginRequestV2 request = LoginRequestV2.builder().identityToken("token이요").fcmToken("fcm토킁이여").build();
		LoginResponse response = LoginResponse.builder().accessToken("accessToken").refreshToken("refreshToken")
			.isNewMember(true).userSettingDone(true).build();
		when(authServiceV2.loginOrSignInV2(any())).thenReturn(response);

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
					fieldWithPath("identityToken").description("애플의 identityToken"),
					fieldWithPath("fcmToken").description("FCM 토큰")
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
