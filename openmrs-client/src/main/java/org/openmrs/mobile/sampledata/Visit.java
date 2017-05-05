package org.openmrs.mobile.sampledata;

import java.io.Serializable;

public class Visit implements Serializable {
	public String start_date;
	public String end_date;
	public String visit_type;
	public int is_active;

	public Visit(String start_date, String end_date, int is_active, String visit_type) {
		this.start_date = start_date;
		this.end_date = end_date;
		this.is_active = is_active;
		this.visit_type = visit_type;
	}

}
