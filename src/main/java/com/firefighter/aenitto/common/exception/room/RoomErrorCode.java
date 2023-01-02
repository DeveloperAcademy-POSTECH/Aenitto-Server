package com.firefighter.aenitto.common.exception.room;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.firefighter.aenitto.common.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum RoomErrorCode implements ErrorCode {
	ROOM_CAPACITY_EXCEEDED(BAD_REQUEST, "방의 수용인원을 초과하였습니다."),
	ROOM_INSUFFICIENT_PARTICIPANTS(BAD_REQUEST, "참여 인원이 5명 이하입니다."),
	ROOM_ALREADY_STARTED(BAD_REQUEST, "이미 시작한 방입니다."),
	ADMIN_CANNOT_EXIT_ROOM(BAD_REQUEST, "방장은 방을 나갈 수 없습니다.\n방 삭제하기를 시도해보세요"),

	ROOM_NOT_PARTICIPATING(FORBIDDEN, "방이 존재하지 않거나 참여 권한이 없는 방입니다."),
	ROOM_UNAUTHORIZED(FORBIDDEN, "시작 권한이 없습니다. \n게임을 시작하려면 방장이어야 합니다"),

	INVITATION_NOT_FOUND(NOT_FOUND, "초대코드가 존재하지 않습니다."),
	ROOM_NOT_FOUND(NOT_FOUND, "방이 존재하지 않습니다."),
	RELATION_NOT_FOUND(NOT_FOUND, "마니또-마니띠 관계가 설정되지 않았습니다."),

	ROOM_ALREADY_PARTICIPATING(CONFLICT, "이미 참여 중인 방입니다."),
	;

	private final HttpStatus status;
	private final String message;
}
