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

public class PersonAttribute extends BaseOpenmrsObject implements Serializable {

	@SerializedName("attributeType")
	@Expose
	private PersonAttributeType attributeType;

	@SerializedName("value")
	@Expose
	private Object value;

	public PersonAttributeType getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(PersonAttributeType attributeType) {
		this.attributeType = attributeType;
	}

	/**
	 * @return The value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value The value
	 */
	public void setValue(Object value) {
		this.value = value;
	}

}

