package com.firefighter.aenitto.auth.domain;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "token_id")
	private Long id;

	/*
	실제 mapping x
	 */
	@Column(name = "member_id")
	private Long memberId;

	@Column
	private String refreshToken;

	@Builder
	public Token(Long memberId, String refreshToken) {
		this.memberId = memberId;
		this.refreshToken = refreshToken;
	}
}
