package com.firefighter.aenitto.auth;

import com.firefighter.aenitto.auth.token.CurrentUserDetails;

import static com.firefighter.aenitto.members.MemberFixture.memberFixture;


public class CurrentUserDetailFixture {
    public static final CurrentUserDetails CURRENT_USER_DETAILS = CurrentUserDetails.of(memberFixture());
}
