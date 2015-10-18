package com.esgi.sslmanager.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	public static final String FILE_DATE_PATTERN = "dd-MM-yyyy_HH:mm:ss";
	public static final String LOG_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FILE_DATE_PATTERN);

	private DateUtils() {}

	public static void switchPattern(String pattern) {
		simpleDateFormat.applyPattern(pattern);
	}

	public static String getDateNow(String pattern) {
		switchPattern(pattern);
		return simpleDateFormat.format(new Date());
	}


	public static Date parseDate(String text, String pattern) {
		try {
			switchPattern(pattern);
			return simpleDateFormat.parse(text);
		} catch (ParseException e) {

		}
		return null;
	}

}
