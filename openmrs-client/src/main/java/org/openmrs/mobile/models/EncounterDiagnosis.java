package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

public class EncounterDiagnosis extends BaseOpenmrsObject {

	@Expose
	private String certainty;
	@Expose
	private String order;
	@Expose
	private String diagnosis;
	@Expose
	private String existingObs;

	public String getCertainty() {
		return certainty;
	}

	public void setCertainty(String certainty) {
		this.certainty = certainty;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getExistingObs() {
		return existingObs;
	}

	public void setExistingObs(String existingObs) {
		this.existingObs = existingObs;
	}
}
