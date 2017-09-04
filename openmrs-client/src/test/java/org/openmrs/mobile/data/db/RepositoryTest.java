package org.openmrs.mobile.data.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.mobile.BuildConfig;
import org.openmrs.mobile.data.DBFlowRule;
import org.openmrs.mobile.data.db.impl.RepositoryImpl;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RepositoryTest {
	@Rule
	public DBFlowRule dbflowTestRule = DBFlowRule.create();

	private Repository repository = new RepositoryImpl();

	@Before
	public void before() {
		CoreTestData.load();
	}

	@After
	public void after() {
		CoreTestData.clear();
	}

	@Test
	public void query_shouldIncludeOperators() throws Exception {

	}
}
