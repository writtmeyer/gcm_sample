package com.grokkingandroid.sampleapp.samples.util;

import java.util.Date;

public class DateUtils {

	public static String formatDateTime(String formatStr, Date date) {
		if (date == null) {
			return null;
		}
    	return android.text.format.DateFormat.format(formatStr, date).toString();
	}
	
	public static String formatDateTimeForIO(Date date) {
    	return formatDateTime("yyyy-MM-dd_hh-mm-ss", date);
	}
	
}
