package org.openmrs.mobile.data.db;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.models.BaseOpenmrsObject;

import java.util.List;

public interface DbService<E extends BaseOpenmrsObject> {
	List<E> getAll(QueryOptions options, PagingInfo pagingInfo);

	E getByUuid(String uuid, QueryOptions options);

	List<E> saveAll(List<E> entities);
	E save(E entity);

	void delete(E entity);
	void delete(String uuid);

}
