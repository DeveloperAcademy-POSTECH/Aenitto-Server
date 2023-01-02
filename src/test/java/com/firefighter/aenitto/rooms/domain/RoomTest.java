package com.firefighter.aenitto.rooms.domain;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class RoomTest {
	@DisplayName("6개의 난수 (0-9 혹은 A-Z의 upper-case) 생성")
	@Test
	public void createRandomSixNumString() throws
		NoSuchMethodException,
		InvocationTargetException,
		IllegalAccessException {
		// given
		Room room = Room.builder().build();
		Method randomSixNumUpperString = Room.class.getDeclaredMethod("randomSixNumUpperString");
		randomSixNumUpperString.setAccessible(true);

		// when
		String random = (String)randomSixNumUpperString.invoke(room);

		// then
		assertThat(random.length()).isEqualTo(6);
		assertThat(random).isUpperCase();
	}

	@DisplayName("Room::isProcessingAndExpired 테스트 - 성공")
	@Test
	void isProcessingAndExpired_success() {
		// given
		Room room1 = Room.builder()
			.endDate(LocalDate.now().minusDays(1))
			.build();

		Room room2 = Room.builder()
			.endDate(LocalDate.now().minusDays(1))
			.build();

		Room room3 = Room.builder()
			.endDate(LocalDate.now())
			.build();

		Room room4 = Room.builder()
			.endDate(LocalDate.now().plusDays(1))
			.build();

		ReflectionTestUtils.setField(room2, "state", RoomState.PROCESSING);

		// when, then
		assertThat(room1.isProcessingAndExpired()).isFalse();
		assertThat(room2.isProcessingAndExpired()).isTrue();
		assertThat(room3.isProcessingAndExpired()).isFalse();
		assertThat(room4.isProcessingAndExpired()).isFalse();
	}
}

