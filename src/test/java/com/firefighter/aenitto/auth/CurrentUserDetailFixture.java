package com.firefighter.aenitto.auth;

import static com.firefighter.aenitto.members.MemberFixture.memberFixture;

import com.firefighter.aenitto.auth.token.CurrentUserDetails;

public class CurrentUserDetailFixture {
	public static final CurrentUserDetails CURRENT_USER_DETAILS = CurrentUserDetails.of(memberFixture());
}
