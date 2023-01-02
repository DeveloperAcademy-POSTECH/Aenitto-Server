package com.firefighter.aenitto.members.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static com.firefighter.aenitto.auth.CurrentUserDetailFixture.CURRENT_USER_DETAILS;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;

import com.firefighter.aenitto.auth.token.CurrentUserDetails;
import com.firefighter.aenitto.common.exception.member.MemberNotFoundException;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class MemberServiceImplTest {
	@InjectMocks
	private MemberServiceImpl target;

	@Mock
	@Qualifier("memberRepositoryImpl")
	private MemberRepository memberRepository;

	private Member member;

	private CurrentUserDetails currentUserDetails;

	@BeforeEach
	void setup() {
		currentUserDetails = CURRENT_USER_DETAILS;
	}

	@Test
	@DisplayName("닉네임 수정 - 성공")
	void set_nickname_success() {
		//given
		String nickname = "바뀐닉네임";
		doReturn(Optional.of(currentUserDetails.getMember())).when(memberRepository).findByMemberId(any());

		//when
		target.setNickname(currentUserDetails.getMember(), nickname);

		//then
		Member member1 = currentUserDetails.getMember();
		assertAll(
			() -> verify(memberRepository).findByMemberId(member1.getId()),
			() -> assertEquals(nickname, member1.getNickname())
		);
	}

	@DisplayName("닉네임 수정 - 실패 / 존재하지 않는 유저의 경우")
	@Test
	void set_nickname_fail_not_found_member() throws Exception {

		// given
		String nickname = "테스트";
		doReturn(Optional.empty()).when(memberRepository).findByMemberId(any());

		// when, then
		assertThrows(MemberNotFoundException.class,
			() -> target.setNickname(currentUserDetails.getMember(), nickname));
	}

}
