package org.openmrs.mobile.data.sync;

import android.support.annotation.NonNull;
import android.util.Log;

import org.openmrs.mobile.dagger.DaggerSyncComponent;
import org.openmrs.mobile.dagger.SyncComponent;
import org.openmrs.mobile.data.sync.impl.DiagnosisConceptSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.LocationSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.PatientListContextSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.PatientListSubscriptionProvider;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class DaggerProviderHelper {
	private static final String DIAGNOSIS_CONCEPT_SUBSCRIPTION = DiagnosisConceptSubscriptionProvider.class.getSimpleName();
	private static final String LOCATION_SUBSCRIPTION = LocationSubscriptionProvider.class.getSimpleName();
	private static final String PATIENT_LIST_CONTEXT_SUBSCRIPTION = PatientListContextSubscriptionProvider.class.getSimpleName();
	private static final String PATIENT_LIST_SUBSCRIPTION = PatientListSubscriptionProvider.class.getSimpleName();

	private SyncComponent syncComponent;

	@Inject
	public DaggerProviderHelper() {
		this.syncComponent = DaggerSyncComponent.builder().build();
	}

	public SubscriptionProvider getSubscriptionProvider(@NonNull String className) {
		checkNotNull(className);

		SubscriptionProvider provider = null;
		if (className.endsWith(DIAGNOSIS_CONCEPT_SUBSCRIPTION)) {
			provider = syncComponent.diagnosisConceptSubscriptionProvider();
		} else if (className.endsWith(LOCATION_SUBSCRIPTION)) {
			provider = syncComponent.locationSubscriptionProvider();
		} else if (className.endsWith(PATIENT_LIST_CONTEXT_SUBSCRIPTION)) {
			provider = syncComponent.patientListContextSubscriptionProvider();
		} else if (className.endsWith(PATIENT_LIST_SUBSCRIPTION)) {
			provider = syncComponent.patientListSubscriptionProvider();
		}

		if (provider == null) {
			Log.e(SyncService.TAG, "Unknown subscription provider '" + className + "'");
		}

		return provider;
	}

	public SyncProvider getSyncProvider(String className) {
		return null;
	}
}
