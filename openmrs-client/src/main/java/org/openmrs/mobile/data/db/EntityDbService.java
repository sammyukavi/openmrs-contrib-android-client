package org.openmrs.mobile.data.db;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.models.BaseOpenmrsEntity;
import org.openmrs.mobile.models.Patient;

import java.util.List;

public interface EntityDbService<E extends BaseOpenmrsEntity> extends DbService<E> {
	List<E> getByPatient(Patient patient, boolean includeInactive, PagingInfo pagingInfo);
}
