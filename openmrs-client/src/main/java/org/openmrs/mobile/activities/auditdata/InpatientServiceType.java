package org.openmrs.mobile.activities.auditdata;

import java.io.Serializable;

public class InpatientServiceType implements Serializable {
	private String name;
	private String uuid;

	public InpatientServiceType(String name, String uuid) {
		this.name = name;
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
