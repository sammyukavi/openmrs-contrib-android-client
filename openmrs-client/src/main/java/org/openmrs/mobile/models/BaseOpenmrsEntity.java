package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;

import java.io.Serializable;
import java.util.Date;

public class BaseOpenmrsEntity extends BaseOpenmrsAuditableObject implements Serializable {
	private static final long serialVersionUID = 1;

	@Expose
	@Column
	private Boolean voided = Boolean.FALSE;

	@Expose
	@Column
	private Date dateVoided;

	@Expose
	@ForeignKey(stubbedRelationship = true)
	private User voidedBy;

	@Expose
	@Column
	private String voidReason;

	public Boolean getVoided() {
		return getActive();
	}

	Boolean isVoided() {
		return getVoided();
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
}
