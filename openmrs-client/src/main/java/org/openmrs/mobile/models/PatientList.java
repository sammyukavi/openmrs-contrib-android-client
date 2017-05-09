/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Model class that represents a Patient List.
 */
public class PatientList extends BaseOpenmrsMetadata implements Serializable{
    private Long id;

    @SerializedName("patientListConditions")
    @Expose
    private List<PatientListCondition> patientListConditions;

    @SerializedName("ordering")
    @Expose
    private List<PatientListOrder> ordering;

    @SerializedName("headerTemplate")
    @Expose
    private String headerTemplate;

    @SerializedName("bodyTemplate")
    @Expose
    private String bodyTemplate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<PatientListCondition> getPatientListConditions() {
        return patientListConditions;
    }

    public void setPatientListConditions(List<PatientListCondition> patientListConditions) {
        this.patientListConditions = patientListConditions;
    }

    public List<PatientListOrder> getOrdering() {
        return ordering;
    }

    public void setOrdering(List<PatientListOrder> ordering) {
        this.ordering = ordering;
    }

    public String getHeaderTemplate() {
        return headerTemplate;
    }

    public void setHeaderTemplate(String headerTemplate) {
        this.headerTemplate = headerTemplate;
    }

    public String getBodyTemplate() {
        return bodyTemplate;
    }

    public void setBodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
    }

    @Override
    public String toString() {
        return getName();
    }
}
