package org.openmrs.mobile.models;

public class VisitAttribute extends BaseOpenmrsAuditableObject{

    private Integer visitAttributeId;
    private Visit visit;
    private String valueReference;
    private VisitAttributeType attributeType;

    public Integer getVisitAttributeId() {
        return visitAttributeId;
    }

    public void setVisitAttributeId(Integer visitAttributeId) {
        this.visitAttributeId = visitAttributeId;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public String getValueReference() {
        return valueReference;
    }

    public void setValueReference(String valueReference) {
        this.valueReference = valueReference;
    }

    public VisitAttributeType getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(VisitAttributeType attributeType) {
        this.attributeType = attributeType;
    }
}
