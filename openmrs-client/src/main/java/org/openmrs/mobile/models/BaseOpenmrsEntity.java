package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;

public class BaseOpenmrsEntity extends BaseOpenmrsAuditableObject implements Serializable {
	private static final long serialVersionUID = 1;

	@Expose
	private Boolean voided = Boolean.FALSE;

	@Expose
	private Date dateVoided;

	@Expose
	private User voidedBy;

	@Expose
	private String voidReason;

	public Boolean getVoided() {
		return getActive();
	}

	public void setVoided(Boolean voided) {
		setActive(voided);
	}

	public Date getDateVoided() {
		return dateVoided;
	}

	public void setDateVoided(Date dateVoided) {
		this.dateVoided = dateVoided;
	}

	public User getVoidedBy() {
		return voidedBy;
	}

	public void setVoidedBy(User voidedBy) {
		this.voidedBy = voidedBy;
	}

	public String getVoidReason() {
		return voidReason;
	}

	public void setVoidReason(String voidReason) {
		this.voidReason = voidReason;
	}

	@Expose
	private Patient patient;

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
}
