package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

public class Provider extends BaseOpenmrsMetadata {

    private Integer providerId;

    @Expose
    private Person person;

    @Expose
    private String identifier;

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
