package org.openmrs.mobile.activities.visit.detail;

public enum EncounterDataType {
	VITALS(0),

	/**
	 * A visit task has been completed.
	 */
	AUDIT_DATA(1);

	private int value;

	private EncounterDataType(int value) {
		this.value = value;
	}
}
