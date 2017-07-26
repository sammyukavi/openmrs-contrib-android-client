package org.openmrs.mobile.data.rest.impl;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.rest.BaseRestService;
import org.openmrs.mobile.data.rest.retrofit.DiagnosisSearchRestService;
import org.openmrs.mobile.models.DiagnosisSearchResult;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import javax.inject.Inject;

import retrofit2.Call;

public class DiagnosisSearchRestServiceImpl extends BaseRestService<DiagnosisSearchResult, DiagnosisSearchRestService> {
	public static final String CONCEPT_SEARCH_PATH = "conceptset";

	@Inject
	public DiagnosisSearchRestServiceImpl() { }

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V2 + "/custom/";
	}

	@Override
	protected String getEntityName() {
		return "diagnoses";
	}

	public Call<Results<DiagnosisSearchResult>> search(String term, PagingInfo pagingInfo) {
		return restService.search(buildRestRequestPath(), term, PagingInfo.getLimit(pagingInfo),
				PagingInfo.getStartIndex(pagingInfo));
	}

	public Call<Results<DiagnosisSearchResult>> getSetConcepts(String setUuid) {
		return restService.getSetConcepts(getRestPath() + CONCEPT_SEARCH_PATH, setUuid);
	}
}
