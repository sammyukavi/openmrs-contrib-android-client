package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.VisitNoteDbService;
import org.openmrs.mobile.data.rest.VisitNoteRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitNote;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class VisitNoteDataService extends BaseDataService<VisitNote, VisitNoteDbService, VisitNoteRestService>
		implements DataService<VisitNote> {

	@Override
	protected VisitNoteDbService getDbService() {
		return new VisitNoteDbService();
	}

	@Override
	protected Class<VisitNoteRestService> getRestServiceClass() {
		return VisitNoteRestService.class;
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V2 + "custom";
	}

	@Override
	protected String getEntityName() {
		return "visitnote";
	}

	@Override
	protected Call<VisitNote> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return null;
	}

	@Override
	protected Call<Results<VisitNote>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return null;
	}

	@Override
	protected Call<VisitNote> _restCreate(String restPath, VisitNote entity) {
		return null;
	}

	@Override
	protected Call<VisitNote> _restUpdate(String restPath, VisitNote entity) {
		return null;
	}

	@Override
	protected Call<VisitNote> _restPurge(String restPath, String uuid) {
		return null;
	}

	public void save(VisitNote visitNote,  @NonNull GetCallback<VisitNote> callback){
		executeSingleCallback(callback, null,
				() -> null,
				() -> {
					Gson gson = new Gson();
					Map<String, String> params = new HashMap<>();
					params.put("personId", visitNote.getPersonId());
					params.put("htmlFormId", visitNote.getHtmlFormId());
					params.put("createVisit", visitNote.getCreateVisit());
					params.put("formModifiedTimestamp", visitNote.getFormModifiedTimestamp());
					params.put("encounterModifiedTimestamp", visitNote.getEncounterModifiedTimestamp());
					params.put("visitId", visitNote.getVisitId());
					params.put("returnUrl", visitNote.getReturnUrl());
					params.put("closeAfterSubmission", visitNote.getCloseAfterSubmission());
					params.put("encounterDiagnoses", gson.toJson(visitNote.getEncounterDiagnoses()));
					params.put("encounterId", visitNote.getEncounterId());
					params.put("w1", visitNote.getW1());
					params.put("w3", visitNote.getW3());
					params.put("w5", visitNote.getW5());
					params.put("w10", visitNote.getW10());
					params.put("w12", visitNote.getW12());

					return restService.save(buildRestRequestPath(), params);
				});
	}
}
