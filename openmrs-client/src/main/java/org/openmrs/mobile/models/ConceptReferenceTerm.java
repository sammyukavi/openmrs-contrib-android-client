package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

public class ConceptReferenceTerm extends BaseOpenmrsObject{

	@Expose
	private String code;
	@Expose
	private ConceptSource conceptSource;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ConceptSource getConceptSource() {
		return conceptSource;
	}

	public void setConceptSource(ConceptSource conceptSource) {
		this.conceptSource = conceptSource;
	}
}
