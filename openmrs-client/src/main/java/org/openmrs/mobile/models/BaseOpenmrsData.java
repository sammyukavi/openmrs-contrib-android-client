package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;

public class BaseOpenmrsData extends BaseOpenmrsAuditableObject implements Serializable {

    @Expose
    private Boolean voided = Boolean.FALSE;

    @Expose
    private Date dateVoided;

    @Expose
    private User voidedBy;

    @Expose
    private String voidReason;

    public Boolean getVoided() {
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
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
