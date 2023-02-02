package com.firefighter.aenitto.common.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConverter {
	final static String DATE_PATTERN = "yyyy.MM.dd";

	public static String localDateToString(LocalDate date) {
		return date.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
	}

	public static LocalDate stringToLocalDate(String date) {
		return LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_PATTERN));
	}
}
