package com.firefighter.aenitto.auth.integration;

import com.firefighter.aenitto.auth.dto.request.TempLoginRequest;
import com.firefighter.aenitto.auth.repository.RefreshTokenRepository;
import com.firefighter.aenitto.common.annotation.CurrentMember;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import com.firefighter.aenitto.support.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthIntegrationTest extends IntegrationTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @DisplayName("로그인 - 성공 / 임시 로그인")
    @ParameterizedTest
    @MethodSource("provideLoginUser")
    void login_success(TempLoginRequest request) throws Exception {

        // given, when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/temp-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Member member = memberRepository.findBySocialId(request.getAccessToken()).get();
        assertAll(
                () -> assertTrue(memberRepository.findBySocialId(request.getAccessToken()).isPresent()),
                () -> assertTrue(refreshTokenRepository.findByMemberId(member.getId()).isPresent())
        );
    }

    private static Stream<Arguments> provideLoginUser() {
        return Stream.of(
                Arguments.of(createTempLoginRequest()),
                Arguments.of(TempLoginRequest.builder()
                                .accessToken("accessToken")
                        .build())
        );
    }

    private static TempLoginRequest createTempLoginRequest() {
        return TempLoginRequest.builder()
                .accessToken("accessToken")
                .build();
    }
}
