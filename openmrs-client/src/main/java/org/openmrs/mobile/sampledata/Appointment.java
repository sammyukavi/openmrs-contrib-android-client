package org.openmrs.mobile.sampledata;

import java.io.Serializable;

public class Appointment implements Serializable {
	public String date;

	public Appointment(String date) {
		this.date = date;
	}

}
