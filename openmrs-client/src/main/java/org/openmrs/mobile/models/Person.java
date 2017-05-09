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

import org.greenrobot.greendao.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

public class Person extends BaseOpenmrsEntity {

	@Transient
	@Expose
	private List<PersonName> names = new ArrayList<PersonName>();
	@Expose
	private String gender;
	@Expose
	private String birthdate;
	@Expose
	private boolean birthdateEstimated;

	@Transient
	@Expose
	private List<PersonAddress> addresses = new ArrayList<PersonAddress>();

	@Transient
	@Expose
	private List<PersonAttribute> personAttributes;

	/**
	 * @return The names
	 */
	public List<PersonName> getNames() {
		return names;
	}

	/**
	 * @param names The names
	 */
	public void setNames(List<PersonName> names) {
		this.names = names;
	}

	public PersonName getName() {
		if (!names.isEmpty()) {
			return names.get(0);
		} else {
			return null;
		}
	}

	/**
	 * @return The gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender The gender
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return The birthdate
	 */
	public String getBirthdate() {
		return birthdate;
	}

	/**
	 * @param birthdate The birthdate
	 */
	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	/**
	 * @return The birthdateEstimated
	 */
	public boolean getBirthdateEstimated() {
		return birthdateEstimated;
	}

	/**
	 * @param birthdateEstimated The birthdate
	 */
	public void setBirthdateEstimated(boolean birthdateEstimated) {
		this.birthdateEstimated = birthdateEstimated;
	}

	/**
	 * @return The addresses
	 */
	public List<PersonAddress> getAddresses() {
		return addresses;
	}

	/**
	 * @param addresses The addresses
	 */
	public void setAddresses(List<PersonAddress> addresses) {
		this.addresses = addresses;
	}

	public PersonAddress getAddress() {
		if (!addresses.isEmpty()) {
			return addresses.get(0);
		} else {
			return null;
		}
	}

	public List<PersonAttribute> getPersonAttributes() {
		return personAttributes;
	}

	public void setPersonAttributes(List<PersonAttribute> personAttributes) {
		this.personAttributes = personAttributes;
	}
}
