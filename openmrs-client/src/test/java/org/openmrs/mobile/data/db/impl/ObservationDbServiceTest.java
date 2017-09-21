package org.openmrs.mobile.data.db.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.mobile.data.CoreTestData;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.data.ModelGenerators;
import org.openmrs.mobile.data.db.BaseAuditableDbServiceTest;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.Arrays;
import java.util.List;

public class ObservationDbServiceTest  extends BaseAuditableDbServiceTest<Observation> {
	private ObsDbService obsDbService;

	@Override
	protected BaseDbService<Observation> getDbService() {
		obsDbService = new ObsDbService(repository);

		return obsDbService;
	}

	@Override
	protected ModelAsserters.ModelAsserter<Observation> getAsserter() {
		return ModelAsserters.OBSERVATION;
	}

	@Override
	protected ModelGenerators.ModelGenerator<Observation> getGenerator() {
		return ModelGenerators.OBSERVATION;
	}

	@Test
	public void getObsByVisitAndConcept_shouldReturnExpectedResults() throws Exception {
		String conceptVisitDocumentUuid = ApplicationConstants.ObservationLocators.VISIT_DOCUMENT_UUID.split(",")[0];
		Observation obs = generator.generate(true);
		obs.getConcept().setUuid(conceptVisitDocumentUuid);

		dbService.save(obs);

		List<Observation> observations = dbService.getAll(null, null);

		Assert.assertEquals(1, observations.size());
		Assert.assertEquals(obs.getUuid(), observations.get(0).getUuid());

		List<Observation> results = obsDbService.getVisitPhotoObservations(obs.getEncounter().getVisit().getUuid(), null);

		Assert.assertEquals(1, results.size());
		Assert.assertEquals(conceptVisitDocumentUuid, results.get(0).getConcept().getUuid());
	}

	@Test
	public void getObsByVisitAndConcept_shouldReturnNoResults() throws Exception {
		Observation obs = generator.generate(true);

		dbService.save(obs);

		List<Observation> observations = dbService.getAll(null, null);

		Assert.assertEquals(1, observations.size());
		Assert.assertEquals(obs.getUuid(), obs.getEncounter().getVisit().getUuid());

		List<Observation> results = obsDbService.getVisitPhotoObservations(obs.getEncounter().getVisit().getUuid(), null);
		Assert.assertEquals(0, results.size());
	}
}