package com.firefighter.aenitto.scratch;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DatetimeTest {

	@DisplayName("String -> LocalDate 테스트")
	@Test
	void stringToLocalDateTest() {
		// given
		String dateString = "2022.06.30";
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

		// when
		LocalDate parse = LocalDate.parse(dateString, dateTimeFormatter);

		// then
		assertThat(parse).isEqualTo(LocalDate.of(2022, 6, 30));
	}

	@DisplayName("LocalDate -> String 테스트")
	@Test
	void LocalDateToStringTest() {
		// given
		LocalDate now = LocalDate.of(2022, 6, 30);
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

		// when
		String format = now.format(dateTimeFormatter);

		// then
		assertThat(format).isEqualTo("2022.06.30");
	}

}
