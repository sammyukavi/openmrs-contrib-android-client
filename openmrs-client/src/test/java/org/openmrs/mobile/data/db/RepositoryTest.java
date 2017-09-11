package org.openmrs.mobile.data.db;

import com.raizlabs.android.dbflow.config.FlowManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.mobile.BuildConfig;
import org.openmrs.mobile.data.CoreTestData;
import org.openmrs.mobile.data.DBFlowRule;
import org.openmrs.mobile.data.db.impl.RepositoryImpl;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Location_Table;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Patient_Table;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.models.PersonName_Table;
import org.openmrs.mobile.models.Person_Table;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.UUID;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RepositoryTest {
	@Rule
	public DBFlowRule dbflowTestRule = DBFlowRule.create();

	private Repository repository = new RepositoryImpl();
	private Location_Table locationTable;
	private Patient_Table patientTable;
	private Person_Table personTable;
	private PersonName_Table personNameTable;

	@Before
	public void before() {
		CoreTestData.load();

		locationTable = (Location_Table)FlowManager.getInstanceAdapter(Location.class);
		patientTable = (Patient_Table)FlowManager.getInstanceAdapter(Patient.class);
		personTable = (Person_Table)FlowManager.getInstanceAdapter(Person.class);
		personNameTable = (PersonName_Table)FlowManager.getInstanceAdapter(PersonName.class);
	}

	@After
	public void after() {
		CoreTestData.clear();
	}

	@Test
	public void query_shouldIncludeOperators() throws Exception {

	}

	@Test
	public void update_shouldUpdateUuidIfChanged() throws Exception {
		// Create a new entity with a local id
		Location location = new Location();
		location.setName("Test Location");

		Assert.assertNotNull(location.getUuid());
		Assert.assertTrue(Location.isLocalUuid(location.getUuid()));

		// Make sure that this entity doesn't already exist in the db
		Location search = repository.querySingle(locationTable, Location_Table.uuid.eq(location.getUuid()));
		Assert.assertNull(search);

		// Save the entity to db and check that it saved properly
		repository.save(locationTable, location);

		search = repository.querySingle(locationTable, Location_Table.uuid.eq(location.getUuid()));
		Assert.assertNotNull(search);
		Assert.assertEquals(location.getUuid(), search.getUuid());

		// Update the uuid and update the entity
		String localUuid = location.getUuid();
		location.setUuid(UUID.randomUUID().toString());
		boolean result = repository.update(locationTable, localUuid, location);
		Assert.assertTrue(result);

		// Try to get the entity using the old uuid
		Location localLocation = repository.querySingle(locationTable, Location_Table.uuid.eq(localUuid));
		Assert.assertNull(localLocation);

		// Get the entity using the new uuid
		search = repository.querySingle(locationTable, Location_Table.uuid.eq(location.getUuid()));
		Assert.assertNotNull(search);
		Assert.assertEquals(location.getUuid(), search.getUuid());
	}

	@Test
	public void update_shouldUpdateChildRecordsIfParentUuidChanges() throws Exception {
		Person person = new Person();
		PersonName name1 = new PersonName();
		name1.setFamilyName("one");
		person.getNames().add(name1);
		PersonName name2 = new PersonName();
		name2.setFamilyName("two");
		person.getNames().add(name2);
		PersonName name3 = new PersonName();
		name3.setFamilyName("three");
		person.getNames().add(name3);

		person.processRelationships();

		Assert.assertNotNull(person.getUuid());
		Assert.assertTrue(Location.isLocalUuid(person.getUuid()));

		repository.save(personTable, person);
		Person search = repository.querySingle(personTable, Person_Table.uuid.eq(person.getUuid()));
		Assert.assertNotNull(search);

		List<PersonName> names = repository.query(personNameTable, PersonName_Table.person_uuid.eq(person.getUuid()));
		Assert.assertEquals(3, names.size());

		String localUuid = person.getUuid();
		person.setUuid(UUID.randomUUID().toString());
		boolean result = repository.update(personTable, localUuid, person);
		Assert.assertTrue(result);

		// No entity or child of the entity should be found with the old uuid
		search = repository.querySingle(personTable, Person_Table.uuid.eq(localUuid));
		Assert.assertNull(search);
		names = repository.query(personNameTable, PersonName_Table.person_uuid.eq(localUuid));
		Assert.assertEquals(0, names.size());

		search = repository.querySingle(personTable, Person_Table.uuid.eq(person.getUuid()));
		Assert.assertNotNull(search);
		Assert.assertEquals(person.getUuid(), search.getUuid());
		names = repository.query(personNameTable, PersonName_Table.person_uuid.eq(person.getUuid()));
		Assert.assertEquals(3, names.size());
	}

	@Test
	public void update_shouldUpdateRelatedRecordsIfParentUuidChanges() throws Exception {
		Patient patient = new Patient();
		Person person = new Person();
		patient.setPerson(person);

		patient.processRelationships();

		Assert.assertNotNull(patient.getUuid());
		Assert.assertTrue(Location.isLocalUuid(patient.getUuid()));
		Assert.assertNotNull(person.getUuid());
		Assert.assertTrue(Location.isLocalUuid(person.getUuid()));

		repository.save(patientTable, patient);
		Patient patientSearch = repository.querySingle(patientTable, Patient_Table.uuid.eq(patient.getUuid()));
		Assert.assertNotNull(patientSearch);
		Person personSearch = repository.querySingle(personTable, Person_Table.uuid.eq(person.getUuid()));
		Assert.assertNotNull(personSearch);
		Assert.assertNotNull(patientSearch.getPerson());
		Assert.assertEquals(person.getUuid(), patientSearch.getPerson().getUuid());

		String localPatientUuid = patient.getUuid();
		String localPersonUuid = person.getUuid();
		patient.setUuid(UUID.randomUUID().toString());
		person.setUuid(UUID.randomUUID().toString());

		boolean result = repository.update(personTable, localPersonUuid, person);
		Assert.assertTrue(result);

		result = repository.update(patientTable, localPatientUuid, patient);
		Assert.assertTrue(result);

		patientSearch = repository.querySingle(patientTable, Patient_Table.uuid.eq(localPatientUuid));
		Assert.assertNull(patientSearch);
		personSearch = repository.querySingle(personTable, Person_Table.uuid.eq(localPersonUuid));
		Assert.assertNull(personSearch);

		patientSearch = repository.querySingle(patientTable, Patient_Table.uuid.eq(patient.getUuid()));
		Assert.assertNotNull(patientSearch);
		personSearch = repository.querySingle(personTable, Person_Table.uuid.eq(person.getUuid()));
		Assert.assertNotNull(personSearch);
		Assert.assertNotNull(patientSearch.getPerson());
		Assert.assertEquals(person.getUuid(), patientSearch.getPerson().getUuid());
	}
}
