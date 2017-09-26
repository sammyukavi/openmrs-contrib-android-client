package org.openmrs.mobile.data.sync.impl;

import org.greenrobot.eventbus.EventBus;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.ConceptDbService;
import org.openmrs.mobile.data.db.impl.RecordInfoDbService;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.impl.ConceptRestServiceImpl;
import org.openmrs.mobile.data.sync.AdaptiveSubscriptionProvider;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.utilities.ApplicationConstants;

import javax.inject.Inject;

public class ConceptSubscriptionProvider extends AdaptiveSubscriptionProvider<Concept, ConceptDbService,
		ConceptRestServiceImpl> {

	@Inject
	public ConceptSubscriptionProvider(ConceptDbService dbService,
			RecordInfoDbService recordInfoDbService,
			ConceptRestServiceImpl restService, Repository repository, EventBus eventBus) {
		super(dbService, recordInfoDbService, restService, repository, eventBus);
	}

	@Override
	public void initialize(PullSubscription subscription) {
		super.initialize(subscription);
	}

	@Override
	public void pull(PullSubscription subscription) {
		Concept concept = RestHelper.getCallValue(restService.getByUuid(ApplicationConstants.AuditFormConcepts
				.CONCEPT_INPATIENT_SERVICE_TYPE, QueryOptions.FULL_REP));
		concept.processRelationships();

		if (concept != null) {
			dbService.save(concept);
		}

	}
}
