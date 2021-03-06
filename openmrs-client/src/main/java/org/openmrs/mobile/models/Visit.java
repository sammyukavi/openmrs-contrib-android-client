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

import org.greenrobot.greendao.annotation.Generated;

import java.util.List;

public class Visit extends BaseOpenmrsEntity {

	private Long id;

	@Expose
	private VisitType visitType;

	@Expose
	private Location location;

	@Expose
	private String startDatetime;

	@Expose
	private String stopDatetime;

	@Expose
	private List<Encounter> encounters;

	@Expose
	private List<VisitAttribute> attributes;

	@Generated(hash = 284896357)
	public Visit(Long id, String startDatetime, String stopDatetime) {
		this.id = id;
		this.startDatetime = startDatetime;
		this.stopDatetime = stopDatetime;
	}

	@Generated(hash = 808752442)
	public Visit() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
