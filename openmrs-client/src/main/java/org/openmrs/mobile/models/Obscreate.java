/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Obscreate extends BaseOpenmrsAuditableObject implements Serializable {

	@SerializedName("formFieldPath")
	@Expose
	private String formFieldPath;

	@SerializedName("concept")
	@Expose
	private String concept;

	@SerializedName("person")
	@Expose
	private String person;

	@SerializedName("order")
	@Expose
	private String order;

	@SerializedName("location")
	@Expose
	private String location;

	@SerializedName("obsDatetime")
	@Expose
	private String obsDatetime;

	@SerializedName("formFieldNamespace")
	@Expose
	private String formFieldNamespace;

	@SerializedName("groupMembers")
	@Expose
	private String groupMembers;

	@SerializedName("value")
	@Expose
	private String value;

	@SerializedName("accessionNumber")
	@Expose
	private String accessionNumber;

	@SerializedName("encounter")
	@Expose
	private String encounter;

	@SerializedName("comment")
	@Expose
	private String comment;

	@SerializedName("valueModifier")
	@Expose
	private String valueModifier;

	@SerializedName("valueCodedName")
	@Expose
	private String valueCodedName;

}
