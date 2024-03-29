package com.firefighter.aenitto.common.exception.mission;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.firefighter.aenitto.common.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum MissionErrorCode implements ErrorCode {
	MISSION_EMPTY(HttpStatus.NOT_FOUND, "미션이 존재하지 않습니다"),
	MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "오늘의 미션이 설정되지 않았습니다"),
	MISSION_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 미션을 찾을 수 없습니다"),
	MISSION_ALREADY_SET(HttpStatus.CONFLICT, "이미 오늘의 미션이 설정되었습니다.");

	private final HttpStatus status;
	private final String message;
}
