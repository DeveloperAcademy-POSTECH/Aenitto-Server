package com.firefighter.aenitto.common.exception.member;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.firefighter.aenitto.common.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {
	MEMBER_NOT_FOUND(NOT_FOUND, "존재하지 않는 사용자 입니다.");

	private final HttpStatus status;
	private final String message;
}
