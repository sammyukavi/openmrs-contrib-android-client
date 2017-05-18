package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Obs {

	@SerializedName("uuid")
	@Expose
	private String uuid;

	@SerializedName("display")
	@Expose
	private String display;

	@SerializedName("concept")
	@Expose
	private Concept concept;

	@SerializedName("obsDatetime")
	@Expose
	private String obsDatetime;

	@SerializedName("value")
	@Expose
	private Value value;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public String getObsDatetime() {
		return obsDatetime;
	}

	public void setObsDatetime(String obsDatetime) {
		this.obsDatetime = obsDatetime;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}
}
