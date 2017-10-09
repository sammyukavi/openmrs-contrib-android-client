package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

@Table(database = AppDatabase.class)
public class VisitAttributeType extends BaseOpenmrsMetadata {
	@Expose
	@SerializedName("minOccurs")
	@Column
	private Integer minOccurs = 0;

	@Expose
	@SerializedName("maxOccurs")
	@Column
	private Integer maxOccurs = null;

	@Expose
	@SerializedName("datatypeClassname")
	@Column
	private String datatypeClassname;

	@Expose
	@SerializedName("datatypeConfig")
	@Column
	private String datatypeConfig;

	@Expose
	@SerializedName("preferredHandlerClassname")
	@Column
	private String preferredHandlerClassname;

	@Expose
	@SerializedName("handlerConfig")
	@Column
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
