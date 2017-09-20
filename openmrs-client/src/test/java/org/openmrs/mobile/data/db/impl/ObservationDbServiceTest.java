package org.openmrs.mobile.data.db.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.mobile.data.CoreTestData;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.data.ModelGenerators;
import org.openmrs.mobile.data.db.BaseAuditableDbServiceTest;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.models.Observation;

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
	public void shouldGetObsByVisitAndConceptList() throws Exception {
		Observation obs = generator.generate(true);
		obs.getConcept().setUuid(CoreTestData.Constants.Concept.VISIT_DOCUMENT_UUID);
		obs.getEncounter().getVisit().setUuid(CoreTestData.Constants.Visit.VISIT_UUID);

		dbService.save(obs);

		Assert.assertEquals(1, dbService.getAll(null, null).size());

		List<Observation> results = obsDbService.getObsByVisitAndConceptList(CoreTestData.Constants.Visit
				.VISIT_UUID, null);
		Assert.assertEquals(1, results.size());
	}
}