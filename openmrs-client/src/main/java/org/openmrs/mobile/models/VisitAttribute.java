package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

@Table(database = AppDatabase.class)
public class VisitAttribute extends BaseOpenmrsEntity {
	@Expose
	@ForeignKey
	private Visit visit;

	@Expose
	private Object value;

	@Expose
	@Column
	private String valueReference;

	@Expose
	@ForeignKey
	private VisitAttributeType attributeType;

	public Visit getVisit() {
		return visit;
	}

	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	public String getValueReference() {
		return valueReference;
	}

	public void setValueReference(String valueReference) {
		this.valueReference = valueReference;
	}

	public VisitAttributeType getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(VisitAttributeType attributeType) {
		this.attributeType = attributeType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
