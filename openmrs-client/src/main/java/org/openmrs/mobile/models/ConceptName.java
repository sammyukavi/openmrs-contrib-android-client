package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

public class ConceptName extends BaseOpenmrsMetadata {

    @Expose
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getName();
    }
}
