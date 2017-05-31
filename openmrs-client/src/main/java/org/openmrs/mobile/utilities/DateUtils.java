/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.utilities;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openmrs.mobile.application.OpenMRS;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public final class DateUtils {
	public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
	public static final String DATE_WITH_TIME_FORMAT = "dd/MM/yyyy HH:mm";
	public static final String OPEN_MRS_REQUEST_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final String OPEN_MRS_REQUEST_PATIENT_FORMAT = "yyyy-MM-dd";
	public static final String PATIENT_DASHBOARD_VISIT_DATE_FORMAT = "dd-MMM-yyyy HH:mm";
	public static final String TIME_FORMAT = "HH:mm";
	public static final String DATE_FORMAT = "dd-MMM-yyyy";
	public static final Long ZERO = 0L;
	private static final String OPEN_MRS_RESPONSE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	private DateUtils() {

	}

	public static String convertTime(long time, String dateFormat, TimeZone timeZone) {
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		format.setTimeZone(timeZone);
		return format.format(date);
	}

	public static String convertTime(long time, String dateFormat) {
		return convertTime(time, dateFormat, TimeZone.getDefault());
	}

	public static String convertTime(long timestamp, TimeZone timeZone) {
		return convertTime(timestamp, DEFAULT_DATE_FORMAT, timeZone);
	}

	public static String convertTime(long timestamp) {
		return convertTime(timestamp, DEFAULT_DATE_FORMAT, TimeZone.getDefault());
	}

	public static Long convertTime(String dateAsString) {
		return convertTime(dateAsString, OPEN_MRS_RESPONSE_FORMAT);
	}

	public static Long convertTime(String dateAsString, String dateFormat) {
		Long time = null;
		if (StringUtils.notNull(dateAsString)) {
			DateFormat format = new SimpleDateFormat(dateFormat);
			Date formattedDate;
			try {
				formattedDate = parseString(dateAsString, format);
				time = formattedDate.getTime();
			} catch (ParseException e) {
				try {
					formattedDate = parseString(dateAsString, new SimpleDateFormat(OPEN_MRS_REQUEST_PATIENT_FORMAT));
					time = formattedDate.getTime();
				} catch (ParseException e1) {
					OpenMRS.getInstance().getOpenMRSLogger()
							.w("Failed to parse date :" + dateAsString + " caused by " + e.toString());
				}
			}
		}
		return time;
	}

	private static Date parseString(String dateAsString, DateFormat format) throws ParseException {
		Date formattedDate = null;
		try {
			formattedDate = format.parse(dateAsString);
		} catch (NullPointerException e) {
			OpenMRS.getInstance().getOpenMRSLogger()
					.w("Failed to parse date :" + dateAsString + " caused by " + e.toString());
		}
		return formattedDate;
	}

	public static DateTime convertTimeString(String dateAsString) {
		DateTime date = null;
		if (StringUtils.notNull(dateAsString)) {
			DateTimeFormatter originalFormat = DateTimeFormat.forPattern(DateUtils.OPEN_MRS_REQUEST_FORMAT);
			date = originalFormat.parseDateTime(dateAsString);
		}
		return date;
	}

	public static String convertTime1(String dateAsString, String dateFormat) {
		if (StringUtils.notNull(dateAsString) && StringUtils.notEmpty(dateAsString)) {
			return convertTime(convertTime(dateAsString), dateFormat, TimeZone.getDefault());
		}
		return dateAsString;
	}

	public static String now(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}

	public static String calculateAge(String date) {
		DateFormat dateFormat = new SimpleDateFormat(OPEN_MRS_REQUEST_FORMAT, Locale.getDefault());

		int years = 0;
		int months = 0;
		int days = 0;
		try {
			Date startDate = dateFormat.parse(date);
			Calendar now = Calendar.getInstance();
			Calendar dob = Calendar.getInstance();
			dob.setTime(startDate);
			if (dob.after(now)) {
				throw new IllegalArgumentException("Can't be born in the future");
			}
			int year1 = now.get(Calendar.YEAR);
			int year2 = dob.get(Calendar.YEAR);
			years = year1 - year2;
			if (years == 0) {
				int month1 = now.get(Calendar.MONTH);
				int month2 = dob.get(Calendar.MONTH);

				if (month1 > month2) {
					months = month1 - month2;
				} else if (month1 == month2) {
					int day1 = now.get(Calendar.DAY_OF_MONTH);
					int day2 = dob.get(Calendar.DAY_OF_MONTH);
					if (day1 > day2) {
						days = day1 - day2;
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (years > 0) {
			return String.valueOf(years);
		} else if (months > 0) {
			return months + " months";
		} else
			return days + " days";
	}

	public static String convertTimeStringDisplay(String dateAsString) {
		DateTime date = null;
		if (StringUtils.notNull(dateAsString)) {
			DateTimeFormatter originalFormat = DateTimeFormat.forPattern(DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT);
			date = originalFormat.parseDateTime(dateAsString);
		}
		return String.valueOf(date);
	}

	public static String calculateRelativeDate(Date pastDate) {
		String relative = "";
		Date today = new Date();
		long days = TimeUnit.MILLISECONDS.toHours(today.getTime() - pastDate.getTime());
		if (days == 0) {
			relative = "today";
		} else if (days < 7) {
			relative = (days == 1 ? "yesterday" : days + " days ago");
		} else if (days < 30) {
			relative = Math.round(days / 7) + " week" + (days / 7 > 1 ? "s " : " ") + "ago";
		} else {
			relative = Math.round(days / 30) + " month" + (days / 30 > 1 ? "s " : " ") + "ago";
		}

		return relative;
	}

	public static String calculateRelativeDate(String dateAsString) {
		String relative = "";
		try {
			Date pastDate = parseString(dateAsString, new SimpleDateFormat(DateUtils.OPEN_MRS_RESPONSE_FORMAT));
			relative = calculateRelativeDate(pastDate);
		} catch (ParseException ex) {
		}

		return relative;
	}

	private static String calculateTimeDifference(String dateStart, String dateStop, boolean minimum) {

		Date startDate = null;
		Date endDate = null;
		SimpleDateFormat format = new SimpleDateFormat(OPEN_MRS_RESPONSE_FORMAT);

		try {
			startDate = format.parse(dateStart);
			endDate = format.parse(dateStop);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}

		long durationInSeconds = TimeUnit.MILLISECONDS.toSeconds(endDate.getTime() - startDate.getTime());

		long SECONDS_IN_A_MINUTE = 60;
		long MINUTES_IN_AN_HOUR = 60;
		long HOURS_IN_A_DAY = 24;
		long DAYS_IN_A_WEEK = 7;
		long DAYS_IN_A_MONTH = 30;
		long MONTHS_IN_A_YEAR = 12;

		long sec = (durationInSeconds >= SECONDS_IN_A_MINUTE) ? durationInSeconds % SECONDS_IN_A_MINUTE : durationInSeconds;
		long min = (durationInSeconds /= SECONDS_IN_A_MINUTE) >= MINUTES_IN_AN_HOUR ?
				durationInSeconds % MINUTES_IN_AN_HOUR :
				durationInSeconds;
		long hrs = (durationInSeconds /= MINUTES_IN_AN_HOUR) >= HOURS_IN_A_DAY ?
				durationInSeconds % HOURS_IN_A_DAY :
				durationInSeconds;
		long days = (durationInSeconds /= HOURS_IN_A_DAY) >= DAYS_IN_A_WEEK ?
				durationInSeconds % DAYS_IN_A_WEEK :
				durationInSeconds;
		long weeks = (durationInSeconds /= DAYS_IN_A_WEEK) >= DAYS_IN_A_MONTH ?
				durationInSeconds % DAYS_IN_A_MONTH :
				durationInSeconds;
		long months = (durationInSeconds /= DAYS_IN_A_MONTH) >= MONTHS_IN_A_YEAR ?
				durationInSeconds % MONTHS_IN_A_YEAR :
				durationInSeconds;
		long years = (durationInSeconds /= MONTHS_IN_A_YEAR);

		return getDuration(sec, min, hrs, days, weeks, months, years, minimum);
	}

	public static String calculateTimeDifference(String startDate, String stopDate) {
		return calculateTimeDifference(startDate, stopDate, false);
	}

	public static String calculateTimeDifference(String startDate, boolean minimum) {
		return calculateTimeDifference(startDate, new SimpleDateFormat(OPEN_MRS_RESPONSE_FORMAT).format(new Date()),
				minimum);
	}

	public static String calculateTimeDifference(String startDate) {
		return calculateTimeDifference(startDate, new SimpleDateFormat(OPEN_MRS_RESPONSE_FORMAT).format(new Date()),
				true);
	}

	private static String getDuration(long secs, long mins, long hrs, long days, long weeks, long months, long years,
			boolean minimum) {
		StringBuffer sb = new StringBuffer();
		String EMPTY_STRING = "";
		if (minimum) {
			if (years > 0)
				sb.append(years > 0 ? years + (years > 1 ? " yrs " : " y ") : EMPTY_STRING);
			else if (months > 0)
				sb.append(months > 0 ? months + (months > 1 ? " ms " : " m ") : EMPTY_STRING);
			else if (months > 0)
				sb.append(weeks > 0 ? weeks + (weeks > 1 ? " wks " : " wk ") : EMPTY_STRING);
			else if (days > 0)
				sb.append(days > 0 ? days + (days > 1 ? " ds " : " d ") : EMPTY_STRING);
			else if (hrs > 0)
				sb.append(hrs > 0 ? hrs + (hrs > 1 ? " hs " : " h ") : EMPTY_STRING);
			else if (mins > 0)
				sb.append(mins > 0 ? mins + (mins > 1 ? " min " : " min ") : EMPTY_STRING);
			else if (secs > 0)
				sb.append(secs > 0 ? secs + (secs > 1 ? " secs " : " secs ") : EMPTY_STRING);
		} else {
			sb.append(years > 0 ? years + (years > 1 ? " years " : " year ") : EMPTY_STRING);
			sb.append(months > 0 ? months + (months > 1 ? " months " : " month ") : EMPTY_STRING);
			sb.append(days > 0 ? days + (days > 1 ? " weeks " : " week ") : EMPTY_STRING);
			sb.append(days > 0 ? days + (days > 1 ? " days " : " day ") : EMPTY_STRING);
			sb.append(hrs > 0 ? hrs + (hrs > 1 ? " hours " : " hour ") : EMPTY_STRING);
			sb.append(mins > 0 ? mins + (mins > 1 ? " mins " : " min ") : EMPTY_STRING);
			//sb.append(secs > 0 ? secs + (secs > 1 ? " secs " : " secs ") : EMPTY_STRING);
		}

		//sb.append("ago");
		return sb.toString();
	}
}
