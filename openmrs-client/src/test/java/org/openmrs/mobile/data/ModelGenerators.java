package org.openmrs.mobile.data;

import org.openmrs.mobile.data.db.CoreTestData;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientIdentifier;
import org.openmrs.mobile.models.PatientIdentifierType;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.PersonAddress;
import org.openmrs.mobile.models.PersonAttribute;
import org.openmrs.mobile.models.PersonAttributeType;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttribute;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.utilities.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ModelGenerators {
	public static final EncounterGenerator ENCOUNTER = new EncounterGenerator();
	public static final LocationGenerator LOCATION = new LocationGenerator();
	public static final PatientGenerator PATIENT = new PatientGenerator();
	public static final PersonGenerator PERSON = new PersonGenerator();
	public static final VisitGenerator VISIT = new VisitGenerator();

	public interface ModelGenerator<E> {
		E generate(boolean createRelations);
	}

	public static class EncounterGenerator implements ModelGenerator<Encounter> {
		private Location location;

		public Location getLocation() {
			return location;
		}

		public void setLocation(Location location) {
			this.location = location;
		}

		@Override
		public Encounter generate(boolean createRelations) {
			Encounter encounter = new Encounter();

			encounter.setEncounterDatetime(new Date(2010, 3, 4));

			if (location == null) {
				location = LOCATION.generate(false);
			}
			encounter.setLocation(location);

			return encounter;
		}
	}

	public static class LocationGenerator implements ModelGenerator<Location> {
		@Override
		public Location generate(boolean createRelations) {
			Location entity = new Location();

			entity.setName("Location 1");
			entity.setAddress1("Address 1");
			entity.setAddress2("Address 2");
			entity.setCityVillage("City Village");
			entity.setCountry("Country");
			entity.setPostalCode("12345");
			entity.setStateProvince("State Province");

			return entity;
		}
	}

	public static class PatientGenerator implements ModelGenerator<Patient> {
		@Override
		public Patient generate(boolean createRelations) {
			Patient entity = new Patient();

			entity.setVoided(false);
			entity.setResourceVersion("1");

			if (createRelations) {
				entity.setPerson(PERSON.generate(true));

				PatientIdentifierType idType = new PatientIdentifierType();
				idType.setUuid(CoreTestData.Constants.PatientIdentifierType.FILE_NUMBER_UUID);

				PatientIdentifier identifier = new PatientIdentifier();
				identifier.setIdentifier("12345");
				identifier.setIdentifierType(idType);
				identifier.setPatient(entity);

				entity.getIdentifiers().add(identifier);
			}

			return entity;
		}
	}

	public static class PersonGenerator implements ModelGenerator<Person> {
		@Override
		public Person generate(boolean createRelations) {
			SimpleDateFormat format = new SimpleDateFormat(DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT);

			Person person = new Person();
			person.setGender("MALE");
			person.setAge(9);

			Date birthdate = new Date(2001, 2, 3);
			person.setBirthdate(format.format(birthdate));

			Date deathdate = new Date(2010, 4, 5);
			person.setDeathDate(format.format(deathdate));

			person.getNames().add(generatePersonName(person, 1));
			person.getNames().add(generatePersonName(person, 2));
			person.getNames().add(generatePersonName(person, 3));

			person.getAddresses().add(generatePersonAddress(person, 1));
			person.getAddresses().add(generatePersonAddress(person, 2));
			person.getAddresses().add(generatePersonAddress(person, 3));

			PersonAttributeType type = new PersonAttributeType();
			type.setUuid(CoreTestData.Constants.PersonAttributeType.PHONE_NUMBER_UUID);
			person.getAttributes().add(generatePersonAttribute(person, type, 1));

			type = new PersonAttributeType();
			type.setUuid(CoreTestData.Constants.PersonAttributeType.WARD_UUID);
			person.getAttributes().add(generatePersonAttribute(person, type, 2));

			return person;
		}

		public PersonName generatePersonName(Person person, int index) {
			PersonName name = new PersonName();

			name.setGivenName("Given Name " + index);
			name.setMiddleName("Middle Name " + index);
			name.setFamilyName("Family Name " + index);

			name.setPerson(person);

			return name;
		}

		public PersonAddress generatePersonAddress(Person person, int index) {
			PersonAddress address = new PersonAddress();

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

		public PersonAttribute generatePersonAttribute(Person person, PersonAttributeType type, int index) {
			PersonAttribute attribute = new PersonAttribute();

			attribute.setStringValue("Something " + index);

			attribute.setAttributeType(type);
			attribute.setPerson(person);

			return attribute;
		}
	}

	public static class VisitGenerator implements ModelGenerator<Visit> {
		private Patient patient;
		private Location location;

		public Patient getPatient() {
			return patient;
		}

		public void setPatient(Patient patient) {
			this.patient = patient;
		}

		public Location getLocation() {
			return location;
		}

		public void setLocation(Location location) {
			this.location = location;
		}

		@Override
		public Visit generate(boolean createRelations) {
			Visit visit = new Visit();

			VisitType type = new VisitType();
			type.setUuid(CoreTestData.Constants.VisitType.INPATIENT_MEDICINE_UUID);

			visit.setVisitType(type);
			visit.setStartDatetime(new Date(2017, 2, 3, 10, 30, 0));
			visit.setStopDatetime(new Date(2017, 2, 3, 13, 00, 0));

			if (patient == null) {
				patient = PATIENT.generate(false);
			}
			visit.setPatient(patient);
			if (location == null) {
				location = LOCATION.generate(false);
			}
			visit.setLocation(location);

			if (createRelations) {
				VisitAttributeType attributeType = new VisitAttributeType();
				attributeType.setUuid(CoreTestData.Constants.VisitAttributeType.BED_NUMBER_UUID);
				visit.getAttributes().add(generateVisitAttribute(visit, attributeType, 0));
			}

			return visit;
		}

		public VisitAttribute generateVisitAttribute(Visit visit, VisitAttributeType type, int index) {
			VisitAttribute attribute = new VisitAttribute();

			attribute.setAttributeType(type);
			attribute.setValueReference(String.valueOf(index));

			attribute.setVisit(visit);

			return attribute;
		}
	}
}
