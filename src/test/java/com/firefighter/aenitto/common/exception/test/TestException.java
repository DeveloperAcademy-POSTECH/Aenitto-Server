package com.firefighter.aenitto.common.exception.test;

import com.firefighter.aenitto.common.exception.CustomException;

public class TestException extends CustomException {
	private static final String MESSAGE = "Refresh 토큰이 저장되어있지 않습니다";
	private static final TestErrorCode code = TestErrorCode.TEST_ERROR_CODE;

	public TestException() {
		super(code);
	}
}
