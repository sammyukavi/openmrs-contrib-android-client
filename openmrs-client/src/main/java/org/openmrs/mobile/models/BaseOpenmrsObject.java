package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseOpenmrsObject extends Resource implements Serializable {
	private static final long serialVersionUID = 1L;

	@SerializedName("active")
	@Expose
	private Boolean active;

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void refreshDaoProperties() {
		// This base method does nothing but should be overriden by models that have links to other models
	}
}
