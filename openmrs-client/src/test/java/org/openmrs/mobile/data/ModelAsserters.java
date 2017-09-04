package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import junit.framework.Assert;

import org.openmrs.mobile.models.BaseOpenmrsAuditableObject;
import org.openmrs.mobile.models.BaseOpenmrsEntity;
import org.openmrs.mobile.models.BaseOpenmrsMetadata;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.User;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttribute;
import org.openmrs.mobile.models.VisitType;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ModelAsserters {
	public static final EncounterAsserter ENCOUNTER = new EncounterAsserter();
	public static final LocationAsserter LOCATION = new LocationAsserter();
	public static final PatientAsserter PATIENT = new PatientAsserter();
	public static final UserAsserter USER = new UserAsserter();
	public static final UuidAsserter UUID = new UuidAsserter();
	public static final VisitAsserter VISIT = new VisitAsserter();
	public static final VisitAttributeAsserter VISIT_ATTRIBUTE = new VisitAttributeAsserter();
	public static final VistTypeAsserter VIST_TYPE = new VistTypeAsserter();

	public interface ModelAsserter<E> {
		void assertModel(E expected, E actual);
	}

	public static abstract class ObjectAsserter<E extends BaseOpenmrsObject> implements ModelAsserter<E> {
		public void assertModel(@NonNull E expected, @NonNull E actual) {
			checkNotNull(expected);
			checkNotNull(actual);

			Assert.assertEquals(expected.getUuid(), actual.getUuid());
			Assert.assertEquals(expected.getDisplay(), actual.getDisplay());
		}

		protected <M> void assertSubModel(@Nullable M expected, @Nullable M actual, ModelAsserter<M> asserter) {
			if (expected == null) {
				Assert.assertNull(actual);
			} else {
				asserter.assertModel(expected, actual);
			}
		}

		protected <M extends BaseOpenmrsObject> void assertSubList(@Nullable List<M> expected, @Nullable List<M> actual) {
			if (expected == null) {
				Assert.assertNull(actual);
			} else {
				Assert.assertNotNull(actual);
				Assert.assertEquals(expected.size(), actual.size());

				for (int i = 0; i<expected.size(); i++) {
					Assert.assertNotNull(expected.get(i));
					Assert.assertNotNull(actual.get(i));

					Assert.assertEquals(expected.get(i).getUuid(), actual.get(i).getUuid());
				}
			}
		}

		protected <M> void assertSubList(@Nullable List<M> expected, @Nullable List<M> actual, ModelAsserter<M> asserter) {
			if (expected == null) {
				Assert.assertNull(actual);
			} else {
				Assert.assertNotNull(actual);
				Assert.assertEquals(expected.size(), actual.size());

				for (int i = 0; i<expected.size(); i++) {
					asserter.assertModel(expected.get(i), actual.get(i));
				}
			}
		}
	}

	public static abstract class AuditableAsserter<E extends BaseOpenmrsAuditableObject> extends ObjectAsserter<E> {
		@Override
		public void assertModel(@NonNull E expected, @NonNull E actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getDateCreated(), actual.getDateCreated());
			Assert.assertEquals(expected.getDateChanged(), actual.getDateChanged());

			assertSubModel(expected.getCreator(), actual.getCreator(), USER);
			assertSubModel(expected.getChangedBy(), actual.getChangedBy(), USER);
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
			Assert.assertEquals(expected.getVoidReason(), actual.getVoidReason());
			Assert.assertEquals(expected.getDateVoided(), actual.getDateVoided());

			assertSubModel(expected.getVoidedBy(), actual.getVoidedBy(), USER);
		}
	}

	public static class UuidAsserter extends ObjectAsserter<BaseOpenmrsObject> {
		@Override
		public void assertModel(@NonNull BaseOpenmrsObject expected, @NonNull BaseOpenmrsObject actual) {
			checkNotNull(expected);
			checkNotNull(actual);

			Assert.assertEquals(expected.getUuid(), actual.getUuid());
		}
	}

	public static class UserAsserter extends ObjectAsserter<User> {
		@Override
		public void assertModel(@NonNull User expected, @NonNull User actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getSystemId(), actual.getSystemId());
			Assert.assertEquals(expected.getUsername(), actual.getUsername());

			assertSubModel(expected.getPerson(), actual.getPerson(), UUID);
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

			Assert.assertEquals(expected.getStartDatetime(), actual.getStartDatetime());
			Assert.assertEquals(expected.getStopDatetime(), actual.getStopDatetime());

			assertSubModel(expected.getPatient(), actual.getPatient(), UUID);
			assertSubModel(expected.getLocation(), actual.getLocation(), UUID);
			assertSubModel(expected.getVisitType(), actual.getVisitType(), UUID);

			assertSubList(expected.getEncounters(), actual.getEncounters(), ENCOUNTER);
			assertSubList(expected.getAttributes(), actual.getAttributes(), VISIT_ATTRIBUTE);
		}
	}

	public static class VistTypeAsserter extends MetadataAsserter<VisitType> {
		@Override
		public void assertModel(@NonNull VisitType expected, @NonNull VisitType actual) {
			super.assertModel(expected, actual);
		}
	}

	public static class VisitAttributeAsserter extends EntityAsserter<VisitAttribute> {
		@Override
		public void assertModel(@NonNull VisitAttribute expected, @NonNull VisitAttribute actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getValueReference(), actual.getValueReference());

			assertSubModel(expected.getVisit(), actual.getVisit(), UUID);
			assertSubModel(expected.getAttributeType(), actual.getAttributeType(), UUID);
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
			Assert.assertEquals(expected.getPostalCode(), actual.getPostalCode());
			Assert.assertEquals(expected.getStateProvince(), actual.getStateProvince());

			if (expected.getParentLocation() == null) {
				Assert.assertNull(actual.getParentLocation());
			} else {
				assertModel(expected.getParentLocation(), actual.getParentLocation());
			}
		}
	}

	public static class EncounterAsserter extends AuditableAsserter<Encounter> {
		@Override
		public void assertModel(@NonNull Encounter expected, @NonNull Encounter actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getEncounterDatetime(), actual.getEncounterDatetime());
			Assert.assertEquals(expected.getVoided(), actual.getVoided());
			Assert.assertEquals(expected.getProvider(), actual.getProvider());
			Assert.assertEquals(expected.getResourceVersion(), actual.getResourceVersion());

			assertSubModel(expected.getPatient(), actual.getPatient(), UUID);
			assertSubModel(expected.getLocation(), actual.getLocation(), UUID);
			assertSubModel(expected.getForm(), actual.getForm(), UUID);
			assertSubModel(expected.getEncounterType(), actual.getEncounterType(), UUID);
			assertSubModel(expected.getVisit(), actual.getVisit(), UUID);

			assertSubList(expected.getObs(), actual.getObs());
			assertSubList(expected.getEncounterProviders(), actual.getEncounterProviders());
		}
	}
}
