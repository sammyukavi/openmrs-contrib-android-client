package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

@Entity
public class BaseOpenmrsObject extends Resource implements Serializable {
    private static final long serialVersionUID = 1;

    @Transient
    @SerializedName("active")
    @Expose
    private Boolean active;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
