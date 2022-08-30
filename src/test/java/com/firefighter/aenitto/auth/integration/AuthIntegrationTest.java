package com.firefighter.aenitto.auth.integration;

import com.firefighter.aenitto.auth.dto.request.LoginRequest;
import com.firefighter.aenitto.auth.repository.RefreshTokenRepository;
import com.firefighter.aenitto.common.exception.auth.AuthErrorCode;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import com.firefighter.aenitto.support.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthIntegrationTest extends IntegrationTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @DisplayName("로그인/회원가입 - 성공")
    @ParameterizedTest
    @MethodSource("provideLoginUser")
    void login_success(LoginRequest request) throws Exception {

        // given, when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.isNewMember").exists())
                .andExpect(jsonPath("$.userSettingDone").exists());
    }

    @DisplayName("로그인/회원가입 - 실패(identityToken유효x)")
    @Test
    void login_failure_invalid_token() throws Exception {

        // given
        LoginRequest invalidTokenRequest = LoginRequest.builder().identityToken("accessToken.test").build();

        // when, then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidTokenRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", AuthErrorCode.INVALID_IDENTITY_TOKEN.getMessage()).exists())
                .andExpect(jsonPath("$.status", AuthErrorCode.INVALID_IDENTITY_TOKEN.getStatus()).exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists());
    }

    private static Stream<Arguments> provideLoginUser() {
        return Stream.of(
                Arguments.of(createLoginRequest()),
                Arguments.of(LoginRequest.builder()
                        .identityToken("eyJraWQiOiJXNldjT0tCIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoiY29tLnJlYm" +
                                "9ybi5saXZlT25SZWJvb3QiLCJleHAiOjE2NjE5NDk4ODUsI" +
                                "mlhdCI6MTY2MTg2MzQ4NSwic3ViIjoiMDAxNzgxLmU4OTljNjQ1YWIxMzQw" +
                                "YTRiZGFhOTIwYzhiOTRiZDMxLjE2MTYiLCJjX2hhc2giOiJONnZCYTJuVVgyYl" +
                                "FiMlZEOFhsUUNBIiwiYXV0aF90aW1lIjoxNjYxODYzNDg1LCJub25jZV9zdXBwb3J0ZWQ" +
                                "iOnRydWV9.IVADTOOjvSIAjXwNgyIGh68WU99PXNyCDrmpoIQBExORdHozWOK5digRKBggr3rMG" +
                                "fvG60bmY7mqEQCAWWwq9v1sEYyZ8Cj3tTlKmdJaL9pNgtqbtzHOc_uFJQYzjn8bB30Yr0EguwkO8sd8vW-" +
                                "gM7HBHXC2b8hdmYg2Cz1nMe7S7SGuFLs87JbqiTCITbAi0OFT8IluZAAqAN5yOD7-zSu21F5OOKZ76lUXHhv" +
                                "5XLEugIDm2j4FSinlFAGSOWS_rKHCPNX_d9zbv9lfbuLOdyTNBKc9LhMHbDqW-lEA_AK0bMRGnypRZwSNK5TQq" +
                                "WcoUdjzDdtbDjjpSGhAfppCuQ")
                        .build())
        );
    }

    private static LoginRequest createLoginRequest() {
        return LoginRequest.builder()
                .identityToken("eyJraWQiOiJXNldjT0tCIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoiY29tLnJlYm" +
                        "9ybi5saXZlT25SZWJvb3QiLCJleHAiOjE2NjE5NDk4ODUsI" +
                        "mlhdCI6MTY2MTg2MzQ4NSwic3ViIjoiMDAxNzgxLmU4OTljNjQ1YWIxMzQw" +
                        "YTRiZGFhOTIwYzhiOTRiZDMxLjE2MTYiLCJjX2hhc2giOiJONnZCYTJuVVgyYl" +
                        "FiMlZEOFhsUUNBIiwiYXV0aF90aW1lIjoxNjYxODYzNDg1LCJub25jZV9zdXBwb3J0ZWQ" +
                        "iOnRydWV9.IVADTOOjvSIAjXwNgyIGh68WU99PXNyCDrmpoIQBExORdHozWOK5digRKBggr3rMG" +
                        "fvG60bmY7mqEQCAWWwq9v1sEYyZ8Cj3tTlKmdJaL9pNgtqbtzHOc_uFJQYzjn8bB30Yr0EguwkO8sd8vW-" +
                        "gM7HBHXC2b8hdmYg2Cz1nMe7S7SGuFLs87JbqiTCITbAi0OFT8IluZAAqAN5yOD7-zSu21F5OOKZ76lUXHhv" +
                        "5XLEugIDm2j4FSinlFAGSOWS_rKHCPNX_d9zbv9lfbuLOdyTNBKc9LhMHbDqW-lEA_AK0bMRGnypRZwSNK5TQq" +
                        "WcoUdjzDdtbDjjpSGhAfppCuQ")
                .build();
    }
}
