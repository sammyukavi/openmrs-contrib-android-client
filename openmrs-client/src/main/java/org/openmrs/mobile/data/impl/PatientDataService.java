package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.PatientDbService;
import org.openmrs.mobile.data.db.impl.PatientListContextDbService;
import org.openmrs.mobile.data.db.impl.PatientListDbService;
import org.openmrs.mobile.data.db.impl.PullSubscriptionDbService;
import org.openmrs.mobile.data.rest.impl.PatientRestServiceImpl;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientListContext;

import java.util.List;

import javax.inject.Inject;

public class PatientDataService extends BaseDataService<Patient, PatientDbService, PatientRestServiceImpl> {

	private PatientListContextDbService patientListContextDbService;
	private PullSubscriptionDbService pullSubscriptionDbService;

	@Inject
	public PatientDataService(PatientListContextDbService patientListContextDbService,
			PullSubscriptionDbService pullSubscriptionDbService) {
		this.patientListContextDbService = patientListContextDbService;
		this.pullSubscriptionDbService = pullSubscriptionDbService;
	}

	public void getByName(String name, QueryOptions options, PagingInfo pagingInfo, GetCallback<List<Patient>> callback) {
		executeMultipleCallback(callback, options, pagingInfo,
				() -> dbService.getByName(name, options, pagingInfo),
				() -> restService.getByName(name, options, pagingInfo)
		);
	}

	public void getByIdentifier(String id, QueryOptions options, PagingInfo pagingInfo,
			GetCallback<List<Patient>> callback) {
		executeMultipleCallback(callback, options, pagingInfo,
				() -> dbService.getByIdentifier(id, options, pagingInfo),
				() -> restService.getByIdentifier(id, options, pagingInfo)
		);
	}

	public void getByNameOrIdentifier(String query, QueryOptions options, PagingInfo pagingInfo,
			GetCallback<List<Patient>> callback) {
		executeMultipleCallback(callback, options, pagingInfo,
				() -> dbService.getByNameOrIdentifier(query, query, options, pagingInfo),
				() -> restService.getByNameOrIdentifier(query, options, pagingInfo)
		);
	}

	public void getLastViewed(String lastViewed, QueryOptions options, PagingInfo pagingInfo,
			GetCallback<List<Patient>> callback) {
		executeMultipleCallback(callback, options, pagingInfo,
				() -> dbService.getLastViewed(options, pagingInfo),
				() -> restService.getLastViewed(lastViewed, options, pagingInfo)
		);
	}

	public boolean isPatientSynced(String id) {
		List<PatientListContext> patientListContexts = patientListContextDbService.getListsForPatient(id);
		for (PatientListContext patientListContext : patientListContexts) {
			Patient patientListPatient = patientListContext.getPatient();
			if (patientListPatient != null && pullSubscriptionDbService.patientListIsSyncing(patientListPatient.getUuid())) {
				return true;
			}
		}
		return false;
	}
}
