package org.openmrs.mobile.utilities.strategy;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.annotations.SerializedName;

public class ObservationExclusionStrategy implements ExclusionStrategy {
	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		SerializedName clazz = f.getAnnotation(SerializedName.class);
		if (clazz != null)
			if (clazz.value() == "uuid" || clazz.value() == "voided" || clazz.value() == "value")
				return false;

		return true;
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}
}
