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

import java.io.Serializable;
import java.util.List;

public class Visit extends BaseOpenmrsEntity implements Serializable {

	@SerializedName("visitType")
	@Expose
	private VisitType visitType;
	@SerializedName("location")
	@Expose
	private Location location;
	@SerializedName("startDatetime")
	@Expose
	private String startDatetime;
	@SerializedName("stopDatetime")
	@Expose
	private String stopDatetime;
	@SerializedName("encounters")
	@Expose
	private List<Encounter> encounters;
	@SerializedName("attributes")
	@Expose
	private List<VisitAttribute> attributes;

	@Expose
	@SerializedName("patient")
	private Patient patient;

	public Visit() {

	}

	public Visit(String visitUuid) {
		this.uuid = visitUuid;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public VisitType getVisitType() {
		return visitType;
	}

	public void setVisitType(VisitType visitType) {
		this.visitType = visitType;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getStartDatetime() {
		return startDatetime;
	}

	public void setStartDatetime(String startDatetime) {
		this.startDatetime = startDatetime;
	}

	public String getStopDatetime() {
		return stopDatetime;
	}

	public void setStopDatetime(String stopDatetime) {
		this.stopDatetime = stopDatetime;
	}

	public List<Encounter> getEncounters() {
		return encounters;
	}

	public void setEncounters(List<Encounter> encounters) {
		this.encounters = encounters;
	}

	public List<VisitAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<VisitAttribute> attributes) {
		this.attributes = attributes;
	}

}