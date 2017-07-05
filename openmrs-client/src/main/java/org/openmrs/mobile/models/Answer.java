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
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.AppDatabase;

import java.io.Serializable;

@Table(database = AppDatabase.class)
public class Answer extends Resource implements Serializable, Parcelable {
	public static final Creator<Answer> CREATOR = new Creator<Answer>() {
		@Override
		public Answer createFromParcel(Parcel source) {
			return new Answer(source);
		}

		@Override
		public Answer[] newArray(int size) {
			return new Answer[size];
		}
	};

	@SerializedName("concept")
	@Expose
	@Column
	private String concept;

	@SerializedName("label")
	@Expose
	@Column
	private String label;

	@ForeignKey(stubbedRelationship = true)
	private QuestionOptions questionOptions;

	public Answer() {
	}

	protected Answer(Parcel in) {
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

	public QuestionOptions getQuestionOptions() {
		return questionOptions;
	}

	public void setQuestionOptions(QuestionOptions questionOptions) {
		this.questionOptions = questionOptions;
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
