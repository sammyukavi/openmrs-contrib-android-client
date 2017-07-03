
package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

public class ObsValue {
	@SerializedName("uuid")
	@Expose
	private String uuid;

	@SerializedName("display")
	@Expose
	private String display;

	@SerializedName("set")
	@Expose
	private Boolean set;

	@SerializedName("version")
	@Expose
	private String version;

	@SerializedName("retired")
	@Expose
	private Boolean retired;

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

	public Boolean getSet() {
		return set;
	}

	public void setSet(Boolean set) {
		this.set = set;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Boolean getRetired() {
		return retired;
	}

	public void setRetired(Boolean retired) {
		this.retired = retired;
	}

}
