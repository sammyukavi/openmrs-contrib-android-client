package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.impl.DiagnosisSearchDbService;
import org.openmrs.mobile.data.rest.impl.DiagnosisSearchRestServiceImpl;
import org.openmrs.mobile.data.sync.AdaptiveSubscriptionProvider;
import org.openmrs.mobile.models.DiagnosisSearchResult;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

import javax.inject.Inject;

public class DiagnosisConceptSubscriptionProvider extends AdaptiveSubscriptionProvider<DiagnosisSearchResult,
		DiagnosisSearchDbService, DiagnosisSearchRestServiceImpl> {
	@Inject
	public DiagnosisConceptSubscriptionProvider() { }

	@Override
	public void initialize(PullSubscription subscription) {
		super.initialize(subscription);
	}

	@Override
	protected long getRecordCountDb() {
		return super.getRecordCountDb();
	}

	@Override
	protected List<DiagnosisSearchResult> getAllRest() {
		return getCallListValue(restService.getSetConcepts(ApplicationConstants.ConceptSets.ICPC_DIAGNOSES));
	}
}
