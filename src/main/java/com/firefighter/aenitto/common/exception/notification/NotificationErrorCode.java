package com.firefighter.aenitto.common.exception.notification;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.firefighter.aenitto.common.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
	FAILED_SENDING_NOTIFICATION(HttpStatus.EXPECTATION_FAILED, "메시지를 보내지 못했습니다.");

	private final HttpStatus status;
	private final String message;
}
