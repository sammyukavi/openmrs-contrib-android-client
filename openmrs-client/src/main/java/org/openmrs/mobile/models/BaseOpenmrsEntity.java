package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class BaseOpenmrsEntity extends BaseOpenmrsAuditableObject implements Serializable {
    private static final long serialVersionUID = 1;

    @Expose
    private Patient patient;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
