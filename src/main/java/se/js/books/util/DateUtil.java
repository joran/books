package se.js.books.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
	public static String format(LocalDateTime date) {
		return date != null ? date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
	}

	public static String format(LocalDate date) {
		return date != null ? date.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
	}

	public static String dateTimeAsString(LocalDateTime date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh.mm, dd MMM yyyy ");
		return date != null ? date.format(formatter) : null;
	}
}
