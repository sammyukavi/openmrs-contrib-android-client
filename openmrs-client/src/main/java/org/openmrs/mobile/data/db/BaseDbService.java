package org.openmrs.mobile.data.db;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.models.BaseOpenmrsObject;

import java.util.List;

public abstract class BaseDbService<E extends BaseOpenmrsObject> implements DbService<E> {
	@Override
	public List<E> getAll(QueryOptions options, PagingInfo pagingInfo) {
		return null;
	}

	@Override
	public E getByUuid(String uuid, QueryOptions options) {
		// Query: SELECT whatevs FROM <TABLE> WHERE uuid = ?
		// Perhaps load related objects?
		// Parse result into entity

		return null;
	}

	@Override
	public List<E> saveAll(List<E> entities) {
		// Start Tx
		// Foreach entity
		//	Save entity
		// End Tx

		return null;
	}

	@Override
	public E save(E entity) {
		return null;
	}

	@Override
	public void delete(E entity) {

	}

	@Override
	public void delete(String uuid) {

	}
}

