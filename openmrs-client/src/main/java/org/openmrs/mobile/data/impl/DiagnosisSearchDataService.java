package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.db.impl.DiagnosisSearchDbService;
import org.openmrs.mobile.data.rest.impl.DiagnosisSearchRestServiceImpl;
import org.openmrs.mobile.models.DiagnosisSearchResult;

import java.util.List;

import javax.inject.Inject;

public class DiagnosisSearchDataService extends BaseDataService<DiagnosisSearchResult, DiagnosisSearchDbService,
		DiagnosisSearchRestServiceImpl> {
	@Inject
	public DiagnosisSearchDataService() { }

	public void search(@NonNull String term, @NonNull PagingInfo pagingInfo,
				GetCallback<List<DiagnosisSearchResult>> callback) {
		executeMultipleCallback(callback, null, pagingInfo,
				() -> null,
				() -> restService.search(term, pagingInfo));
	}

	public void getSetConcepts(@NonNull String setUuid, GetCallback<List<DiagnosisSearchResult>> callback) {
		executeMultipleCallback(callback, null, null,
				() -> null,
				() -> restService.getSetConcepts(setUuid));
	}
}
