package org.openmrs.mobile.utilities;

public interface Consumer<T> {
	void accept(T value);
}
