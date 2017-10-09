package org.openmrs.mobile.utilities;

public interface Function<R, T> {
	T apply(R value);
}
