package org.openmrs.mobile.data;

import android.support.annotation.NonNull;

import junit.framework.Assert;

import org.openmrs.mobile.models.BaseOpenmrsAuditableObject;
import org.openmrs.mobile.models.BaseOpenmrsEntity;
import org.openmrs.mobile.models.BaseOpenmrsMetadata;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;

import static com.google.common.base.Preconditions.checkNotNull;

public class ModelAsserters {
	public static final PatientAsserter PATIENT = new PatientAsserter();
	public static final VisitAsserter VISIT = new VisitAsserter();
	public static final LocationAsserter LOCATION = new LocationAsserter();

	public static abstract class ObjectAsserter<E extends BaseOpenmrsObject> {
		public void assertModel(@NonNull E expected, @NonNull E actual) {
			checkNotNull(expected);
			checkNotNull(actual);

			Assert.assertEquals(expected.getUuid(), actual.getUuid());
			Assert.assertEquals(expected.getDisplay(), actual.getDisplay());
		}
	}

	public static abstract class AuditableAsserter<E extends BaseOpenmrsAuditableObject> extends ObjectAsserter<E> {
		@Override
		public void assertModel(@NonNull E expected, @NonNull E actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getCreator(), actual.getCreator());
			Assert.assertEquals(expected.getDateCreated(), actual.getDateCreated());
			Assert.assertEquals(expected.getChangedBy(), actual.getChangedBy());
			Assert.assertEquals(expected.getDateChanged(), actual.getDateChanged());
		}
	}

	public static abstract class MetadataAsserter<E extends BaseOpenmrsMetadata> extends AuditableAsserter<E> {
		@Override
		public void assertModel(@NonNull E expected, @NonNull E actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getName(), actual.getName());
			Assert.assertEquals(expected.getDescription(), actual.getDescription());
		}
	}

	public static abstract class EntityAsserter<E extends BaseOpenmrsEntity> extends AuditableAsserter<E> {
		@Override
		public void assertModel(@NonNull E expected, @NonNull E actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getVoided(), actual.getVoided());
			Assert.assertEquals(expected.getVoidedBy(), actual.getVoidedBy());
			Assert.assertEquals(expected.getVoidReason(), actual.getVoidReason());
			Assert.assertEquals(expected.getDateVoided(), actual.getDateVoided());
		}
	}

	public static class PatientAsserter extends AuditableAsserter<Patient> {
		@Override
		public void assertModel(@NonNull Patient expected, @NonNull Patient actual) {
			super.assertModel(expected, actual);

			// Assert patient fields
		}
	}

	public static class VisitAsserter extends EntityAsserter<Visit> {
		@Override
		public void assertModel(@NonNull Visit expected, @NonNull Visit actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getPatient(), actual.getPatient());
			Assert.assertEquals(expected.getLocation(), actual.getLocation());
			Assert.assertEquals(expected.getStartDatetime(), actual.getStartDatetime());
			Assert.assertEquals(expected.getStopDatetime(), actual.getStopDatetime());
			Assert.assertEquals(expected.getVisitType(), actual.getVisitType());
		}
	}

	public static class LocationAsserter extends MetadataAsserter<Location> {
		@Override
		public void assertModel(@NonNull Location expected, @NonNull Location actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getAddress1(), actual.getAddress1());
			Assert.assertEquals(expected.getAddress2(), actual.getAddress2());
			Assert.assertEquals(expected.getCityVillage(), actual.getCityVillage());
			Assert.assertEquals(expected.getCountry(), actual.getCountry());
			Assert.assertEquals(expected.getParentLocation(), actual.getParentLocation());
			Assert.assertEquals(expected.getPostalCode(), actual.getPostalCode());
			Assert.assertEquals(expected.getStateProvince(), actual.getStateProvince());
		}
	}
}
