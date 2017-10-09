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
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

/**
 * Represents {@link PatientList} model data
 */
@Table(database = AppDatabase.class)
public class PatientListContext extends BaseOpenmrsObject {
	@SerializedName("patient")
	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Patient patient;

	@SerializedName("visit")
	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Visit visit;

	@SerializedName("patientList")
	@Expose
	@ForeignKey(stubbedRelationship = true)
	private PatientList patientList;

	@SerializedName("headerContent")
	@Expose
	@Column
	private String headerContent;

	@SerializedName("bodyContent")
	@Expose
	@Column
	private String bodyContent;

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Visit getVisit() {
		return visit;
	}

	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	public PatientList getPatientList() {
		return patientList;
	}

	public void setPatientList(PatientList patientList) {
		this.patientList = patientList;
	}

	public String getHeaderContent() {
		return headerContent;
	}

	public void setHeaderContent(String headerContent) {
		this.headerContent = headerContent;
	}

	public String getBodyContent() {
		return bodyContent;
	}

	public void setBodyContent(String bodyContent) {
		this.bodyContent = bodyContent;
	}
}
