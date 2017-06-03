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
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

import java.io.Serializable;
import java.util.List;

@Table(database = AppDatabase.class)
public class Visit extends BaseOpenmrsEntity implements Serializable {
	@Expose
	@ForeignKey
	private VisitType visitType;

	@Expose
	@ForeignKey
	private Location location;

	@Expose
	@Column
	private String startDatetime;

	@Expose
	@Column
	private String stopDatetime;

	@Expose
	private List<Encounter> encounters;

	@Expose
	private List<VisitAttribute> attributes;

	@Expose
	@SerializedName("patient")
	@ForeignKey(stubbedRelationship = true)
	private Patient patient;

	@SerializedName("auditInfo")
	@Expose
	private AuditInfo auditInfo;

	@OneToMany(methods = { OneToMany.Method.ALL}, variableName = "encounters", isVariablePrivate = true)
	List<Encounter> loadEncounters() {
		return loadRelatedObject(Encounter.class, encounters,
				() -> PatientListOrder_Table.patientList_uuid.eq(super.getUuid()));
	}

	@Override
	protected void processRelationships() {
		super.processRelationships();

		processRelatedObjects(encounters);
		processRelatedObjects(attributes);
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

	public AuditInfo getAuditInfo() {
		return auditInfo;
	}

	public void setAuditInfo(AuditInfo auditInfo) {
		this.auditInfo = auditInfo;
	}

}