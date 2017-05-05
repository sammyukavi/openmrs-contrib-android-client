package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

@Entity
public class BaseOpenmrsEntity extends BaseOpenmrsAuditableObject implements Serializable {
	private static final long serialVersionUID = 1;

	@Transient
	@Expose
	private Patient patient;

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
}
