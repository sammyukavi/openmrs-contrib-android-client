package org.openmrs.mobile.data.db;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openmrs.mobile.models.PatientIdentifierType;
import org.openmrs.mobile.models.PatientIdentifierType_Table;
import org.openmrs.mobile.models.PersonAttributeType;
import org.openmrs.mobile.models.PersonAttributeType_Table;

import java.util.ArrayList;
import java.util.List;

public class CoreTestData {
	private static List<PersonAttributeType> personAttributeTypes = new ArrayList<>(3);
	private static List<PatientIdentifierType> patientIdentifierTypes = new ArrayList<>(2);

	public static PersonAttributeType_Table personAttributeTypeTable =
			(PersonAttributeType_Table)FlowManager.getInstanceAdapter(PersonAttributeType.class);
	public static PatientIdentifierType_Table patientIdentifierTypeTable =
			(PatientIdentifierType_Table)FlowManager.getInstanceAdapter(PatientIdentifierType.class);


	public static void load() {
		if (personAttributeTypes.size() == 0) {
			loadPersonAttributeTypes();
		}
		personAttributeTypeTable.saveAll(personAttributeTypes);

		if (patientIdentifierTypes.size() == 0) {
			loadPatientIdentifierTypes();
		}
		patientIdentifierTypeTable.saveAll(patientIdentifierTypes);
	}

	public static void clear() {
		SQLite.delete(PersonAttributeType.class).execute();
		SQLite.delete(PatientIdentifierType.class).execute();
	}

	private static void loadPersonAttributeTypes() {
		PersonAttributeType type1 = new PersonAttributeType();
		type1.setUuid(Constants.PersonAttributeType.PHONE_NUMBER_UUID);
		type1.setDisplay("Phone Number");
		type1.setName("Phone Number");
		type1.setDescription("The telephone number for the person");
		type1.setFormat("java.lang.String");
		type1.setSortWeight(5d);
		type1.setSearchable(false);

		PersonAttributeType type2 = new PersonAttributeType();
		type2.setUuid(Constants.PersonAttributeType.WARD_UUID);
		type2.setDisplay("Ward");
		type2.setName("Ward");
		type2.setFormat("java.lang.String");
		type2.setSortWeight(4d);
		type2.setSearchable(false);

		PersonAttributeType type3 = new PersonAttributeType();
		type3.setUuid(Constants.PersonAttributeType.CIVIL_STATUS_UUID);
		type3.setDisplay("Civil Status");
		type3.setName("Civil Status");
		type3.setDescription("Marriage status of this person");
		type3.setFormat("org.openmrs.Concept");
		type3.setSortWeight(0d);
		type3.setSearchable(false);

		personAttributeTypes.add(type1);
		personAttributeTypes.add(type2);
		personAttributeTypes.add(type3);
	}

	private static void loadPatientIdentifierTypes() {
		PatientIdentifierType type1 = new PatientIdentifierType();

		type1.setUuid(Constants.PatientIdentifierType.FILE_NUMBER_UUID);
		type1.setDisplay("File Number");
		type1.setName("File Number");
		type1.setDescription("Patient file number");
		type1.setRequired(true);
		type1.setCheckDigit(false);

		PatientIdentifierType type2 = new PatientIdentifierType();

		type2.setUuid(Constants.PatientIdentifierType.OPENMRS_ID_UUID);
		type2.setDisplay("OpenMRS ID");
		type2.setName("OpenMRS ID");
		type2.setDescription("OpenMRS patient identifier, with check-digit");
		type2.setRequired(false);
		type2.setCheckDigit(false);

		patientIdentifierTypes.add(type1);
		patientIdentifierTypes.add(type2);
	}

	public static class Constants {
		public static class PersonAttributeType {
			public static String PHONE_NUMBER_UUID = "14d4f066-15f5-102d-96e4-000c29c2a5d7";
			public static String WARD_UUID = "18e3b47a-666d-4173-85a2-8044f8841f0c";
			public static String CIVIL_STATUS_UUID = "8d871f2a-c2cc-11de-8d13-0010c6dffd0f";
		}

		public static class PatientIdentifierType {
			public static String FILE_NUMBER_UUID = "cf54df63-db87-4dea-a14d-dfc44e6d65e5";
			public static String OPENMRS_ID_UUID = "05a29f94-c0ed-11e2-94be-8c13b969e334";
		}
	}
}
