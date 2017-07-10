package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.ConceptSearchDbService;
import org.openmrs.mobile.data.rest.ConceptSearchRestService;
import org.openmrs.mobile.models.ConceptSearchResult;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

import retrofit2.Call;

public class ConceptSearchDataService extends BaseDataService<ConceptSearchResult, ConceptSearchDbService,
		ConceptSearchRestService> {

	@Override
	protected ConceptSearchDbService getDbService() {
		return new ConceptSearchDbService();
	}

	@Override
	protected Class<ConceptSearchRestService> getRestServiceClass() {
		return ConceptSearchRestService.class;
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V2 + "/custom/";
	}

	@Override
	protected String getEntityName() {
		return "diagnoses";
	}

	@Override
	protected Call<ConceptSearchResult> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return null;
	}

	@Override
	protected Call<Results<ConceptSearchResult>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return null;
	}

	@Override
	protected Call<ConceptSearchResult> _restCreate(String restPath, ConceptSearchResult entity) {
		return null;
	}

	@Override
	protected Call<ConceptSearchResult> _restUpdate(String restPath, ConceptSearchResult entity) {
		return null;
	}

	@Override
	protected Call<ConceptSearchResult> _restPurge(String restPath, String uuid) {
		return null;
	}

	public void search(@NonNull String term, @NonNull PagingInfo pagingInfo,
			GetCallback<List<ConceptSearchResult>> callback) {
		executeMultipleCallback(callback, null, pagingInfo,
				() -> null,
				() -> restService.search(buildRestRequestPath(), term, PagingInfo.getStartIndex(pagingInfo),
						PagingInfo.getLimit(pagingInfo)));
	}
}
