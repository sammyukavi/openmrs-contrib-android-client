package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

public class ConceptSource extends BaseOpenmrsMetadata {

	@Expose
	private Integer conceptSourceId;
	@Expose
	private String hl7Code;

	public Integer getConceptSourceId() {
		return conceptSourceId;
	}

	public void setConceptSourceId(Integer conceptSourceId) {
		this.conceptSourceId = conceptSourceId;
	}

	public String getHl7Code() {
		return hl7Code;
	}

	public void setHl7Code(String hl7Code) {
		this.hl7Code = hl7Code;
	}
}
