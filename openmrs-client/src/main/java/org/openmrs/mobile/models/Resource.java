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

import android.support.annotation.Nullable;

import com.google.common.base.Supplier;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openmrs.mobile.utilities.Consumer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Resource implements Serializable {
	private static final long serialVersionUID = 1;
	@SerializedName("uuid")
	@Expose
	@PrimaryKey
	@Column
	protected String uuid;
	@SerializedName("display")
	@Expose
	@Column
	protected String display;
	@SerializedName("links")
	@Expose
	protected List<Link> links = new ArrayList<Link>();
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return The uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid The uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return The display
	 */
	public String getDisplay() {
		return display;
	}

	/**
	 * @param display The display
	 */
	public void setDisplay(String display) {
		this.display = display;
	}

	/**
	 * @return The links
	 */
	public List<Link> getLinks() {
		return links;
	}

	/**
	 * @param links The links
	 */
	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public void processRelationships() {
	}

	public void loadRelatedObjects() {
	}

	protected <R extends Resource> void processRelatedObjects(@Nullable List<R> resources) {
		processRelatedObjects(resources, null);
	}

	protected <R extends Resource> void processRelatedObjects(@Nullable List<R> resources, @Nullable Consumer<R> process) {
		if (resources != null && !resources.isEmpty()) {
			for (R r : resources) {
				if (process != null) {
					process.accept(r);
				}

				r.processRelationships();
			}
		}
	}

	protected <E> List<E> loadRelatedObject(Class<E> cls, List<E> field, Supplier<SQLOperator> op) {
		if (field == null || field.isEmpty()) {
			field = SQLite.select()
					.from(cls)
					.where(op.get())
					.queryList();
		}

		return field;
	}

	public static String generateUuid() {
		return UUID.randomUUID().toString();
	}
}
