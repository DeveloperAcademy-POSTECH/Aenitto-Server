package com.firefighter.aenitto.members.service;

import com.firefighter.aenitto.auth.token.CurrentUserDetails;
import com.firefighter.aenitto.common.exception.member.MemberNotFoundException;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.firefighter.aenitto.auth.CurrentUserDetailFixture.CURRENT_USER_DETAILS;
import static com.firefighter.aenitto.members.MemberFixture.MEMBER_1;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceImplTest {
    @InjectMocks
    private MemberServiceImpl target;

    @Mock
    private MemberRepository memberRepository;

    private Member member;

    private CurrentUserDetails currentUserDetails;

    @BeforeEach
    void setup() {
        member = MEMBER_1;
        currentUserDetails = CURRENT_USER_DETAILS;
    }

    @Test
    @DisplayName("닉네임 수정 - 성공")
    void set_nickname_success(){
        //given
        String nickname = "바뀐 닉네임";
        doReturn(Optional.of(currentUserDetails.getMember())).when(memberRepository).findByMemberId(any());

        //when
        target.setNickname(member, nickname);

        //then
        Member member = currentUserDetails.getMember();
        assertAll(
                () -> verify(memberRepository).findByMemberId(member.getId()),
                ()-> assertEquals(nickname, member.getNickname())
        );
    }

    @DisplayName("유저 성함 저장 - 실패 / 존재하지 않는 유저의 경우")
    @Test
    void set_nickname_fail_not_found_member() throws Exception {

        // given
        String nickname = "테스트";
        doReturn(Optional.empty()).when(memberRepository).findByMemberId(any());

        // when, then
        assertThrows(MemberNotFoundException.class,
                () -> target.setNickname(member, nickname));
    }

}
