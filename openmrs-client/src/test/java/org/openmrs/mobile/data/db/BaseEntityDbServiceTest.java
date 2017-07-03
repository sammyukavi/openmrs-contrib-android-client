package org.openmrs.mobile.data.db;

import org.junit.Test;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.models.BaseOpenmrsEntity;

public abstract class BaseEntityDbServiceTest<E extends BaseOpenmrsEntity> extends BaseAuditableDbServiceTest<E> {
	@Test
	public void getByPatient_shouldSearchByPatient() throws Exception {

	}

	@Test
	public void getByPatient_shouldReturnMultipleEntities() throws Exception {

	}

	@Test
	public void getByPatient_shouldReturnSingleEntity() throws Exception {

	}

	@Test
	public void getByPatient_shouldReturnEmptyListWhenNoEntities() throws Exception {

	}

	@Test
	public void getByPatient_shouldReturnPagedResults() throws Exception {

	}

	@Test
	public void getByPatient_shouldThrowExceptionWhenPatientIsNull() throws Exception {

	}
}
