package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.impl.VisitDbService;
import org.openmrs.mobile.data.rest.impl.VisitRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.Visit;

import javax.inject.Inject;

import retrofit2.Call;

public class VisitPushProvider extends BasePushProvider<Visit, VisitDbService, VisitRestServiceImpl> {
	private VisitRestServiceImpl restService;

	@Inject
	public VisitPushProvider(VisitDbService dbService, VisitRestServiceImpl restService) {
		super(dbService, restService);
		this.restService = restService;
	}

	@Override
	protected void deleteLocalRelatedRecords(Visit originalEntity, Visit restEntity) {
		dbService.deleteLocalRelatedObjects(originalEntity);
	}

	@Override
	protected Call<Visit> update(Visit entity) {
		return restService.updateVisit(entity);
	}

	@Override
	protected Call<Visit> purge(Visit entity) {
		return restService.endVisit(entity.getUuid(), entity);
	}
}
