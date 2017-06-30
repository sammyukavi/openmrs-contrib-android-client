package org.openmrs.mobile.data.db;

import org.junit.Test;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.models.BaseOpenmrsMetadata;

public abstract class BaseMetadataDbServiceTest<E extends BaseOpenmrsMetadata> extends BaseAuditableDbServiceTest<E> {
	@Test
	public void getByNameFragment_shouldSearchNameStartsWithValue() throws Exception {

	}

	@Test
	public void getByNameFragment_shouldReturnMultipleEntities() throws Exception {

	}

	@Test
	public void getByNameFragment_shouldReturnSingleEntity() throws Exception {

	}

	@Test
	public void getByNameFragment_shouldReturnEmptyListWhenNoEntitiesFound() throws Exception {

	}

	@Test
	public void getByNameFragment_shouldReturnPagedResults() throws Exception {

	}

	@Test
	public void getByNameFragment_shouldThrowExceptionWhenNameIsNull() throws Exception {

	}
}
