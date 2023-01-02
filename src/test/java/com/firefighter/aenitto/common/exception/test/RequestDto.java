package com.firefighter.aenitto.common.exception.test;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class RequestDto {
	@NotNull
	private String name;
	@NotNull
	@Min(0)
	@Max(100)
	private int age;

	public RequestDto(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public RequestDto() {
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}
}
