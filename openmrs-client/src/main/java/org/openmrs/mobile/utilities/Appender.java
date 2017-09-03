package org.openmrs.mobile.utilities;

import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.Where;

public interface Appender<T> {
	T accept(T value);
}
