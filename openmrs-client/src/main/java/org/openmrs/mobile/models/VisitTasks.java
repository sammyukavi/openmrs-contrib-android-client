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

package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class VisitTasks extends BaseOpenmrsMetadata {

	@SerializedName("status")
	@Expose
	private VisitTaskStatus status;

	@SerializedName("closedBy")
	@Expose
	private User closedBy;

	@SerializedName("closedOn")
	@Expose
	private Date closedOn;

	@SerializedName("visit")
	@Expose
	private Visit visit;

	@SerializedName("patient")
	@Expose
	private Patient patient;

	public VisitTaskStatus getStatus() {
		return status;
	}

	public void setStatus(VisitTaskStatus status) {
		this.status = status;
	}

	public User getClosedBy() {
		return closedBy;
	}

	public void setClosedBy(User closedBy) {
		this.closedBy = closedBy;
	}

	public Date getClosedOn() {
		return closedOn;
	}

	public void setClosedOn(Date closedOn) {
		this.closedOn = closedOn;
	}

	public Visit getVisit() {
		return visit;
	}

	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
}
