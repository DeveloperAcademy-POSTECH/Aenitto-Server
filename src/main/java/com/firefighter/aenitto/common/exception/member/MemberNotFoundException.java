package com.firefighter.aenitto.common.exception.member;

import com.firefighter.aenitto.common.exception.CustomException;

public class MemberNotFoundException extends CustomException {
	private static final MemberErrorCode CODE = MemberErrorCode.MEMBER_NOT_FOUND;

	private MemberNotFoundException(MemberErrorCode errorCode) {
		super(errorCode);
	}

	public MemberNotFoundException() {
		this(CODE);
	}
}
