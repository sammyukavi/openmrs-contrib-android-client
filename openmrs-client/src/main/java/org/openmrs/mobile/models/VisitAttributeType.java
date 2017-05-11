package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VisitAttributeType extends BaseOpenmrsMetadata {

    @Expose
    @SerializedName("minOccurs")
    private Integer minOccurs = 0;

    @Expose
    @SerializedName("maxOccurs")
    private Integer maxOccurs = null;

    @Expose
    @SerializedName("datatypeClassname")
    private String datatypeClassname;

    @Expose
    @SerializedName("datatypeConfig")
    private String datatypeConfig;

    @Expose
    @SerializedName("preferredHandlerClassname")
    private String preferredHandlerClassname;

    @Expose
    @SerializedName("handlerConfig")
    private String handlerConfig;

    public Integer getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(Integer minOccurs) {
        this.minOccurs = minOccurs;
    }

    public Integer getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(Integer maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public String getDatatypeClassname() {
        return datatypeClassname;
    }

    public void setDatatypeClassname(String datatypeClassname) {
        this.datatypeClassname = datatypeClassname;
    }

    public String getDatatypeConfig() {
        return datatypeConfig;
    }

    public void setDatatypeConfig(String datatypeConfig) {
        this.datatypeConfig = datatypeConfig;
    }

    public String getPreferredHandlerClassname() {
        return preferredHandlerClassname;
    }

    public void setPreferredHandlerClassname(String preferredHandlerClassname) {
        this.preferredHandlerClassname = preferredHandlerClassname;
    }

    public String getHandlerConfig() {
        return handlerConfig;
    }

    public void setHandlerConfig(String handlerConfig) {
        this.handlerConfig = handlerConfig;
    }
}
