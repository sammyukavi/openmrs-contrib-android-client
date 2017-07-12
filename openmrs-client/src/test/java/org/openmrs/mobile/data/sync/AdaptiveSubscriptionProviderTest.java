package org.openmrs.mobile.data.sync;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.models.BaseOpenmrsObject;

@RunWith(MockitoJUnitRunner.class)
public abstract class AdaptiveSubscriptionProviderTest<E extends BaseOpenmrsObject> {
	@Mock
	DbService<E> dbService;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Before
	public void before() {

	}

	@Test
	public void pull_should() throws Exception {

	}
}
