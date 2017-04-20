package org.openmrs.mobile.data.db;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.models.BaseOpenmrsEntity;
import org.openmrs.mobile.models.Patient;

import java.util.List;

public abstract class BaseEntityDbService<E extends BaseOpenmrsEntity> extends BaseDbService<E>
		implements EntityDbService<E> {
	@Override
	public List<E> getByPatient(Patient patient, boolean includeInactive, PagingInfo pagingInfo) {
		return null;
	}
}

