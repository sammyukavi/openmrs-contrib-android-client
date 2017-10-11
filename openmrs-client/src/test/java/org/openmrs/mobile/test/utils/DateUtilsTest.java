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

package org.openmrs.mobile.test.utils;


import org.junit.Before;
import org.junit.Test;
import org.openmrs.mobile.utilities.DateUtils;

import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

public class DateUtilsTest{
	private static final String INITIAL_DATA_1;
	private static final String INITIAL_DATA_2;
	private static final String EXPECTED_DATA_1;
	private static final String EXPECTED_DATA_2;
	private static final String INVALID_DATA_3;
	private static final String INVALID_DATA_4;
	private static final String EXPECTED_LONG_3;

	static {
		INITIAL_DATA_1 = "1967-03-26T00:00:00.000+0200";
		EXPECTED_DATA_1 = "26/03/1967";
		INITIAL_DATA_2 = "1990-03-24T00:00";
		EXPECTED_DATA_2 = "24/03/1990";

		INVALID_DATA_3 = "09-07-1697T00:00";
		EXPECTED_LONG_3 = "598597200000";
		INVALID_DATA_4 = "1988/12/20";
	}

	@Test
	public void calculateAgeShouldReturn_FiveDays(){
		String age = DateUtils.calculateAge("2017-10-06");
		assertEquals("5 days",String.valueOf(age));
	}

	@Test
	public void calculateAgeShouldReturn_ThreeMonths(){
		String age = DateUtils.calculateAge("2017-06-11");
//		assertEquals("3 months",String.valueOf(age));
	}

	@Test
	public void calculateAgeShouldReturn_TwentySixYears(){
		//birthday not passed
		String age = DateUtils.calculateAge("1990-11-25");
//		assertEquals("26",String.valueOf(age));
	}

	@Test
	public void shouldReturnAge_TwentySevenYears(){
		//birthday passed
		String age = DateUtils.calculateAge("1990-09-09");
		assertEquals("27",String.valueOf(age));
	}

	@Test
	public void testDateUtils() {
		Long stringToLongResult;
		String longToStringResult;

		stringToLongResult = DateUtils.convertTime(INITIAL_DATA_1);
		longToStringResult = DateUtils.convertTime(stringToLongResult, TimeZone.getTimeZone("GMT+02:00"));
		assertEquals(EXPECTED_DATA_1, longToStringResult);

		stringToLongResult = DateUtils.convertTime(INITIAL_DATA_2, "yyyy-MM-dd'T'HH:mm");
		longToStringResult = DateUtils.convertTime(stringToLongResult);
		assertEquals(EXPECTED_DATA_2, longToStringResult);

		stringToLongResult = DateUtils.convertTime(INVALID_DATA_3);
		assertNotSame(EXPECTED_LONG_3, String.valueOf(stringToLongResult));

		stringToLongResult = DateUtils.convertTime(INVALID_DATA_4);
		assertNull(stringToLongResult);

		String calculatedDate = DateUtils.calculateAge("2017-10-10");
		assertEquals("1 day",calculatedDate);


	}
}
