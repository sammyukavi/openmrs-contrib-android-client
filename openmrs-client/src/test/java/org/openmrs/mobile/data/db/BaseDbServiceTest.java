package org.openmrs.mobile.data.db;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.mobile.BuildConfig;
import org.openmrs.mobile.data.DBFlowRule;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public abstract class BaseDbServiceTest<E extends BaseOpenmrsObject> {
	@Rule
	public DBFlowRule dbflowTestRule = DBFlowRule.create();

	protected BaseDbService<E> dbService;
	protected ModelAsserters.ModelAsserter<E> asserter;

	protected abstract BaseDbService<E> getDbService();

	protected abstract ModelAsserters.ModelAsserter<E> getAsserter();

	protected abstract E createEntity();

	@Before
	public void setUp() throws Exception {
		this.dbService = getDbService();
		this.asserter = getAsserter();
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void getAll_shouldLoadAllEntities() throws Exception {

	}

	@Test
	public void getAll_shouldLoadRelatedEntities() throws  Exception {

	}

	@Test
	public void getAll_shouldLoadPagedEntities() throws Exception {

	}

	@Test
	public void getAll_shouldReturnEmptyListWhenNoEntities() throws Exception {

	}

	@Test
	public void getByUuid_shouldReturnCorrectEntity() throws Exception {

	}

	@Test
	public void getByUuid_shouldReturnNullWhenNoEntityFound() throws Exception {

	}

	@Test
	public void getByUuid_shouldThrowExceptionWithNullUuid() throws Exception {

	}

	@Test
	public void saveAll_shouldSaveAllEntities() throws Exception {

	}

	@Test
	public void saveAll_shouldSaveSingleEntity() throws Exception {

	}

	@Test
	public void saveAll_shouldDoNothingWithEmptyList() throws Exception {

	}

	@Test
	public void saveAll_shouldThrowExceptionWithNullList() throws Exception {

	}

	@Test
	public void save_shouldInsertNewEntity() throws Exception {

	}

	@Test
	public void save_shouldUpdateExistingEntity() throws Exception {

	}

	@Test
	public void save_shouldThrowExceptionWhenEntityIsNull() throws Exception {

	}

	@Test
	public void delete_shouldDeleteEntity() throws Exception {

	}

	@Test
	public void delete_shouldNotThrowWhenEntityDoesNotExist() throws Exception {

	}

	@Test
	public void delete_shouldThrowExceptionWhenEntityIsNull() throws Exception {

	}
}