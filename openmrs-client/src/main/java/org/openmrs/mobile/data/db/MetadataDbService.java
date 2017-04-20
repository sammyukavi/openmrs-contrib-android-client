package org.openmrs.mobile.data.db;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.models.BaseOpenmrsMetadata;

import java.util.List;

public interface MetadataDbService<E extends BaseOpenmrsMetadata> extends DbService<E> {
	List<E> getByNameFragment(String name, boolean includeInactive, PagingInfo pagingInfo);
}
