package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

public class VisitAttribute extends BaseOpenmrsEntity {

	@Expose
	private Visit visit;

	@Expose
	private Object value;

	@Expose
	private String valueReference;

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
