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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Answers implements Serializable, Parcelable {

	public static final Creator<Answers> CREATOR = new Creator<Answers>() {
		@Override
		public Answers createFromParcel(Parcel source) {
			return new Answers(source);
		}

		@Override
		public Answers[] newArray(int size) {
			return new Answers[size];
		}
	};
	@SerializedName("concept")
	@Expose
	private String concept;
	@SerializedName("label")
	@Expose
	private String label;

	public Answers() {
	}

	protected Answers(Parcel in) {
		this.concept = in.readString();
		this.label = in.readString();
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.concept);
		dest.writeString(this.label);
	}
}
