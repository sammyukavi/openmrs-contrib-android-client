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

public class PersonAttribute extends BaseOpenmrsData {

	@Expose
	private Person person;

	@Expose
	private Object value;

	@Expose
	private String valueReference;

	@Expose
	private PersonAttributeType personAttributeType;

	public String getValueReference() {
		return valueReference;
	}

	public void setValueReference(String valueReference) {
		this.valueReference = valueReference;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public PersonAttributeType getPersonAttributeType() {
		return personAttributeType;
	}

	public void setPersonAttributeType(PersonAttributeType personAttributeType) {
		this.personAttributeType = personAttributeType;
	}
}

