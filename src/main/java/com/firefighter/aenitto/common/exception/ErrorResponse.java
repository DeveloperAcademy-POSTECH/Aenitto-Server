package com.firefighter.aenitto.common.exception;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ErrorResponse {
	private final LocalDateTime timestamp = LocalDateTime.now();
	private final int status;
	private final String message;
	private final List<FieldError> errors;

	public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(errorCode));
	}

	public static ResponseEntity<ErrorResponse> toResponseEntity(BindingResult result) {
		return ResponseEntity.badRequest().body(new ErrorResponse((result)));
	}

	public static ResponseEntity<ErrorResponse> toResponseEntity(MaxUploadSizeExceededException result) {
		return ResponseEntity.badRequest().body(new ErrorResponse((result)));
	}

	private ErrorResponse(ErrorCode errorCode) {
		this.status = errorCode.getStatus().value();
		this.message = errorCode.getMessage();
		this.errors = Collections.emptyList();
	}

	private ErrorResponse(BindingResult result) {
		this.status = HttpStatus.BAD_REQUEST.value();
		this.message = "입력 조건에 대한 예외입니다";
		this.errors = FieldError.of(result);
	}

	private ErrorResponse(MaxUploadSizeExceededException result) {
		this.status = HttpStatus.BAD_REQUEST.value();
		this.message = "최대 2MB의 파일크기를 초과하였습니다";
		this.errors = Collections.emptyList();
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	private static class FieldError {
		private String field;
		private String value;
		private String reason;

		private static List<FieldError> of(BindingResult bindingResult) {
			List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
			return fieldErrors.stream()
				.map(error -> new FieldError(
					error.getField(),
					error.getRejectedValue() != null ? error.getRejectedValue().toString() : null,
					Objects.equals(error.getCode(), "typeMismatch")
						? "입력 값에 대한 예외 입니다" : error.getDefaultMessage()
				))
				.collect(Collectors.toList());
		}
	}
}
