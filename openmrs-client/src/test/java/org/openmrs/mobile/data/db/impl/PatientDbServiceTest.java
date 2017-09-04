package org.openmrs.mobile.data.db.impl;

import org.junit.Test;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.data.db.BaseAuditableDbServiceTest;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.CoreTestData;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientIdentifier;
import org.openmrs.mobile.models.PatientIdentifierType;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.PersonAddress;
import org.openmrs.mobile.models.PersonAttribute;
import org.openmrs.mobile.models.PersonAttributeType;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.utilities.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PatientDbServiceTest extends BaseAuditableDbServiceTest<Patient> {
	@Override
	protected BaseDbService<Patient> getDbService() {
		return new PatientDbService(new RepositoryImpl());
	}

	@Override
	protected ModelAsserters.ObjectAsserter<Patient> getAsserter() {
		return ModelAsserters.PATIENT;
	}

	@Override
	protected Patient createEntity(boolean createSubEntities) {
		Patient entity = new Patient();

		entity.setUuid(UUID.randomUUID().toString());
		entity.setVoided(false);
		entity.setResourceVersion("1");

		if (createSubEntities) {
			entity.setPerson(createPerson());

			PatientIdentifierType idType = new PatientIdentifierType();
			idType.setUuid(CoreTestData.Constants.PatientIdentifierType.FILE_NUMBER_UUID);

			PatientIdentifier identifier = new PatientIdentifier();
			identifier.setUuid(UUID.randomUUID().toString());
			identifier.setIdentifier("12345");
			identifier.setIdentifierType(idType);
			identifier.setPatient(entity);

			entity.getIdentifiers().add(identifier);
		}

		return entity;
	}

	@Test
	public void getByName_shouldSearchByNameWithFullWildcard() throws Exception {

	}

	@Test
	public void getByName_shouldReturnMultipleEntities() throws Exception {

	}

	@Test
	public void getByName_shouldReturnSingleEntity() throws Exception {

	}

	@Test
	public void getByName_shouldReturnEmptyListWhenNoEntities() throws Exception {

	}

	@Test
	public void getByName_shouldReturnPagedResults() throws Exception {

	}

	@Test
	public void getByName_shouldThrowExceptionWhenNameIsNull() throws Exception {

	}

	@Test
	public void getByIdentifier_shouldSearchByIdWithExactMatch() throws Exception {

	}

	@Test
	public void getByIdentifier_shouldReturnSingleEntity() throws Exception {

	}

	@Test
	public void getByIdentifier_shouldReturnNullWhenNoEntityFound() throws Exception {

	}

	@Test
	public void getByIdentifier_shouldThrowExceptionWhenIdentifierIsNull() throws Exception {

	}

	@Test
	public void getLastViewed_shouldRequirePaging() throws Exception {

	}

	@Test
	public void getLastViewed_shouldReturnPagedResults() throws Exception {

	}

	@Test
	public void getLastViewed_shouldReturnResultsInCorrectOrder() throws Exception {

	}

	@Test
	public void getByNameOrIdentifier_shouldSearchByExactIdOrNameWithWildcard() throws Exception {

	}

	@Test
	public void getByNameOrIdentifier_shouldReturnMultipleResults() throws Exception {

	}

	@Test
	public void getByNameOrIdentifier_shouldReturnSingleResult() throws Exception {

	}

	@Test
	public void getByNameOrIdentifier_shouldReturnEmptyListWhenNoResults() throws Exception {

	}

	@Test
	public void getByNameOrIdentifier_shouldReturnPagedResults() throws Exception {

	}

	@Test
	public void getByNameOrIdentifier_shouldThrowExceptionWhenNameOrIdIsNull() throws Exception {

	}

	private Person createPerson() {
		SimpleDateFormat format = new SimpleDateFormat(DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT);

		Person person = new Person();
		person.setUuid(UUID.randomUUID().toString());
		person.setGender("MALE");
		person.setAge(9);

		Date birthdate = new Date(2001, 2, 3);
		person.setBirthdate(format.format(birthdate));

		Date deathdate = new Date(2010, 4, 5);
		person.setDeathDate(format.format(deathdate));

		person.getNames().add(createPersonName(person, 1));
		person.getNames().add(createPersonName(person, 2));
		person.getNames().add(createPersonName(person, 3));

		person.getAddresses().add(createPersonAddress(person, 1));
		person.getAddresses().add(createPersonAddress(person, 2));
		person.getAddresses().add(createPersonAddress(person, 3));

		PersonAttributeType type = new PersonAttributeType();
		type.setUuid(CoreTestData.Constants.PersonAttributeType.PHONE_NUMBER_UUID);
		person.getAttributes().add(createPersonAttribute(person, type, 1));

		type = new PersonAttributeType();
		type.setUuid(CoreTestData.Constants.PersonAttributeType.WARD_UUID);
		person.getAttributes().add(createPersonAttribute(person, type, 2));

		return person;
	}

	private PersonName createPersonName(Person person, int index) {
		PersonName name = new PersonName();

		name.setUuid(UUID.randomUUID().toString());
		name.setGivenName("Given Name " + index);
		name.setMiddleName("Middle Name " + index);
		name.setFamilyName("Family Name " + index);

		name.setPerson(person);

		return name;
	}

	private PersonAddress createPersonAddress(Person person, int index) {
		PersonAddress address = new PersonAddress();

		address.setUuid(UUID.randomUUID().toString());
		address.setPreferred(index == 1);
		address.setAddress1("Address1 " + index);
		address.setAddress2("Address2 " + index);
		address.setCityVillage("City Village " + index);
		address.setStateProvince("State Province " + index);
		address.setCountry("Country " + index);
		address.setPostalCode("Postal Code " + index);

		address.setPerson(person);

		return address;
	}

	private PersonAttribute createPersonAttribute(Person person, PersonAttributeType type, int index) {
		PersonAttribute attribute = new PersonAttribute();

		attribute.setUuid(UUID.randomUUID().toString());
		attribute.setStringValue("Something " + index);

		attribute.setAttributeType(type);
		attribute.setPerson(person);

		return attribute;
	}
}