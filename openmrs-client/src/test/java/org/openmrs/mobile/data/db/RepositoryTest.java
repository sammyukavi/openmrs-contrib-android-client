package org.openmrs.mobile.data.db;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.mobile.BuildConfig;
import org.openmrs.mobile.data.DBFlowRule;
import org.openmrs.mobile.data.db.impl.RepositoryImpl;
import org.openmrs.mobile.models.Privilege;
import org.openmrs.mobile.models.Privilege_Table;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.UUID;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RepositoryTest {
	@Rule
	public DBFlowRule dbflowTestRule = DBFlowRule.create();

	private Repository repository;

	@Before
	public void Setup() {
			/*FlowManager.init(FlowConfig.builder(OpenMRS.getInstance())
					.addDatabaseConfig(
							DatabaseConfig.inMemoryBuilder(TestAppDatabase.class)
							.databaseName("TestDatabase")
							.build())
					.build());
		*/

		repository = new RepositoryImpl();
	}

	@Test
	public void query_shouldIncludeOperators() throws Exception {
		Privilege_Table table = (Privilege_Table)FlowManager.getInstanceAdapter(Privilege.class);

		Privilege p1 = new Privilege();
		p1.setUuid(UUID.randomUUID().toString());
		p1.setPrivilege("Priv 1");

		Privilege p2 = new Privilege();
		p2.setUuid(UUID.randomUUID().toString());
		p2.setPrivilege("Priv 2");

		Privilege p3 = new Privilege();
		p3.setUuid(UUID.randomUUID().toString());
		p3.setPrivilege("Priv 3");

		table.insert(p1);
		table.insert(p2);
		table.insert(p3);

		From<Privilege> from = SQLite.select().from(Privilege.class);

		List<Privilege> results = repository.query(from);
		Assert.assertEquals(3, results.size());

		from.where(Privilege_Table.privilege.like("%3"));
		results = repository.query(from);

		Assert.assertEquals(1, results.size());
		Assert.assertEquals(p3.getUuid(), results.get(0));
	}
}
