package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.ConceptSearchDbService;
import org.openmrs.mobile.data.rest.impl.ConceptSearchRestServiceImpl;
import org.openmrs.mobile.data.rest.retrofit.ConceptSearchRestService;
import org.openmrs.mobile.models.ConceptSearchResult;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

import retrofit2.Call;

public class ConceptSearchDataService extends BaseDataService<ConceptSearchResult, ConceptSearchDbService,
		ConceptSearchRestServiceImpl> {
	public void search(String term, GetCallback<List<ConceptSearchResult>> callback){
		executeMultipleCallback(callback, null, null,
				() -> null,
				() -> restService.search(term));
	}
}
