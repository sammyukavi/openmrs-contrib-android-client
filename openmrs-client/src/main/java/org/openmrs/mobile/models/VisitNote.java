package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class VisitNote extends BaseOpenmrsEntity {

	@Expose
	private String personId;
	@Expose
	private String htmlFormId;
	@Expose
	private String createVisit;
	@Expose
	private String formModifiedTimestamp;
	@Expose
	private String encounterModifiedTimestamp;
	@Expose
	private String visitId;
	@Expose
	private String returnUrl;
	@Expose
	private String closeAfterSubmission;
	@Expose
	private List<EncounterDiagnosis> encounterDiagnoses;
	@Expose
	private String encounterId;
	@Expose
	private String w1;
	@Expose
	private String w3;
	@Expose
	private String w5;
	@Expose
	private String w10;
	@Expose
	private String w12;

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getHtmlFormId() {
		return htmlFormId;
	}

	public void setHtmlFormId(String htmlFormId) {
		this.htmlFormId = htmlFormId;
	}

	public String getCreateVisit() {
		return createVisit;
	}

	public void setCreateVisit(String createVisit) {
		this.createVisit = createVisit;
	}

	public String getFormModifiedTimestamp() {
		return formModifiedTimestamp;
	}

	public void setFormModifiedTimestamp(String formModifiedTimestamp) {
		this.formModifiedTimestamp = formModifiedTimestamp;
	}

	public String getEncounterModifiedTimestamp() {
		return encounterModifiedTimestamp;
	}

	public void setEncounterModifiedTimestamp(String encounterModifiedTimestamp) {
		this.encounterModifiedTimestamp = encounterModifiedTimestamp;
	}

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getCloseAfterSubmission() {
		return closeAfterSubmission;
	}

	public void setCloseAfterSubmission(String closeAfterSubmission) {
		this.closeAfterSubmission = closeAfterSubmission;
	}

	public List<EncounterDiagnosis> getEncounterDiagnoses() {
		return encounterDiagnoses;
	}

	public void setEncounterDiagnoses(List<EncounterDiagnosis> encounterDiagnoses) {
		this.encounterDiagnoses = encounterDiagnoses;
	}

	public String getW1() {
		return w1;
	}

	public void setW1(String w1) {
		this.w1 = w1;
	}

	public String getW3() {
		return w3;
	}

	public void setW3(String w3) {
		this.w3 = w3;
	}

	public String getW5() {
		return w5;
	}

	public void setW5(String w5) {
		this.w5 = w5;
	}

	public String getW10() {
		return w10;
	}

	public void setW10(String w10) {
		this.w10 = w10;
	}

	public String getW12() {
		return w12;
	}

	public void setW12(String w12) {
		this.w12 = w12;
	}

	public String getEncounterId() {
		return encounterId;
	}

	public void setEncounterId(String encounterId) {
		this.encounterId = encounterId;
	}

	public void addEncounterDiagnosis(EncounterDiagnosis encounterDiagnosis){
		if(encounterDiagnoses == null){
			encounterDiagnoses = new ArrayList<>();
		}

		encounterDiagnoses.add(encounterDiagnosis);
	}
}
