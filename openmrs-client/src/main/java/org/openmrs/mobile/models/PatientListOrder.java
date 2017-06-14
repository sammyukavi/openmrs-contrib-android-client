/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Model class that represents the ordering of {@link PatientList}s.
 */
public class PatientListOrder extends Resource {

	@SerializedName("identifiers")
	@Expose
	private PatientList patientList;

	@SerializedName("field")
	@Expose
	private String field;

	@SerializedName("sortOrder")
	@Expose
	private String sortOrder;

	public PatientList getPatientList() {
		return patientList;
	}

	public void setPatientList(PatientList patientList) {
		this.patientList = patientList;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
}
