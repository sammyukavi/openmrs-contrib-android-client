package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VisitAttribute extends BaseOpenmrsEntity {
	@SerializedName("visit")
	@Expose
	private Visit visit;

	@SerializedName("value")
	@Expose
	private Object value;

	@SerializedName("valueReference")
	@Expose
	private String valueReference;

	@SerializedName("attributeType")
	@Expose
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
