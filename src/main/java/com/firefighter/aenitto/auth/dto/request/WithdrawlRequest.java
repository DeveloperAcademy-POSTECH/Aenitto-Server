package com.firefighter.aenitto.auth.dto.request;

import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class WithdrawlRequest {

	@NotBlank
	private final boolean withdrawl;

	@Builder
	public WithdrawlRequest(boolean withDrawl){
		this.withdrawl = withDrawl;
	}
}
