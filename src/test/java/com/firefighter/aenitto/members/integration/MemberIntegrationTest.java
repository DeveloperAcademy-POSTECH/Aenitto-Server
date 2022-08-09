package com.firefighter.aenitto.members.integration;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.dto.request.ChangeNicknameRequest;
import com.firefighter.aenitto.members.repository.MemberRepository;
import com.firefighter.aenitto.members.service.MemberService;
import com.firefighter.aenitto.support.IntegrationTest;
import com.firefighter.aenitto.support.security.WithMockCustomMember;
import org.h2.mvstore.tx.TransactionStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockCustomMember
public class MemberIntegrationTest extends IntegrationTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

//    @DisplayName("멤버 닉네임 변경 - 성공")
//    @Test
//    void set_nickname_success() throws Exception {
//
//        // given
//        ChangeNicknameRequest request = createSetNicknameRequest();
//
//        // when
//        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/members/nickname")
//                        .content(objectMapper.writeValueAsString(request))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNoContent());
//
//        // then
//        flushAndClear();
//        Member member = memberRepository.findBySocialId("socialId").orElseThrow();
//        assertEquals(request.getNickname(), member.getNickname());
//    }

    private ChangeNicknameRequest createSetNicknameRequest() {
        return ChangeNicknameRequest.builder()
                .nickname("테스트")
                .build();
    }
}
