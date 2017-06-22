package org.openmrs.mobile.data.db;

import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperListener;
import com.raizlabs.android.dbflow.structure.database.OpenHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.mobile.BuildConfig;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public abstract class BaseDbServiceTest<E extends BaseOpenmrsObject> {
	protected BaseDbService<E> dbService;

	protected abstract BaseDbService<E> getDbService();

	protected abstract ModelAsserters.ObjectAsserter<E> getAsserter();

	@Before
	public void setUp() throws Exception {
		this.dbService = getDbService();

		/*FlowManager.init(new FlowConfig.Builder(RuntimeEnvironment.application)
				.addDatabaseConfig(new DatabaseConfig.Builder(AppDatabase.class)
						.openHelper(new DatabaseConfig.OpenHelperCreator() {
							@Override
							public OpenHelper createHelper(DatabaseDefinition databaseDefinition,
									DatabaseHelperListener helperListener) {
								return new CustomFlowSQliteOpenHelper();
							}
						}).build())
				.build());*/
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void getAll_shouldLoadAllEntities() throws Exception {

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