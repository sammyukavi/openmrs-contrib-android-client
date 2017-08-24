package org.openmrs.mobile.dagger;

import org.greenrobot.eventbus.EventBus;
import org.openmrs.mobile.data.sync.SyncService;
import org.openmrs.mobile.data.sync.impl.ConceptClassSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.DiagnosisConceptSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.EncounterTypeSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.LocationSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.PatientIdentifierTypeSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.PatientListContextSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.PatientListSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.PersonAttributeTypeSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.VisitAttributeTypeSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.VisitPredefinedTaskSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.VisitTypeSubscriptionProvider;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { DbModule.class, SyncModule.class })
public interface SyncComponent {
	SyncService syncService();

	DiagnosisConceptSubscriptionProvider diagnosisConceptSubscriptionProvider();

	LocationSubscriptionProvider locationSubscriptionProvider();

	PatientListContextSubscriptionProvider patientListContextSubscriptionProvider();

	PatientListSubscriptionProvider patientListSubscriptionProvider();

	ConceptClassSubscriptionProvider conceptClassSubscriptionProvider();

	EncounterTypeSubscriptionProvider encounterTypeSubscriptionProvider();

	PatientIdentifierTypeSubscriptionProvider patientIdentifierTypeSubscriptionProvider();

	PersonAttributeTypeSubscriptionProvider personAttributeTypeSubscriptionProvider();

	VisitAttributeTypeSubscriptionProvider visitAttributeTypeSubscriptionProvider();

	VisitPredefinedTaskSubscriptionProvider visitPredefinedTaskSubscriptionProvider();

	VisitTypeSubscriptionProvider visitTypeSubscriptionProvider();
}
