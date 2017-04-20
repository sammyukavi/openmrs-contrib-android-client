package org.openmrs.mobile.data.db;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.models.BaseOpenmrsObject;

import java.util.List;

public abstract class BaseDbService<E extends BaseOpenmrsObject> implements DbService<E> {
	@Override
	public List<E> getAll(PagingInfo pagingInfo) {
		return null;
	}

	@Override
	public E getByUuid(String uuid) {
		return null;
	}

	@Override
	public List<E> saveAll(List<E> entities) {
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

