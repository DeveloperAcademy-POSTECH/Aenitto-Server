package com.firefighter.aenitto.missions.domain;

import lombok.Getter;

@Getter
public enum MissionType {
	COMMON("COMMON"),
	INDIVIDUAL("INDIVIDUAL"),
	CUSTOM_INDIVIDUAL("CUSTOM_INDIVIDUAL");

	private final String value;

	MissionType(String value) {
		this.value = value;
	}
}
