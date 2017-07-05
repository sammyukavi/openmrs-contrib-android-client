package org.openmrs.mobile.activities;

import org.openmrs.mobile.models.ConceptSearchResult;
import org.openmrs.mobile.models.EncounterDiagnosis;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitNote;

import java.util.List;

public interface IBaseDiagnosisView {

	void showTabSpinner(boolean show);

	void saveVisitNote(String encounterUuid, String clinicalNote, Visit visit);

	void createEncounterDiagnosis(Observation observation, String diagnosis, String conceptNameId);

	void setPrimaryDiagnosis(EncounterDiagnosis primaryDiagnosis);

	void setSecondaryDiagnosis(EncounterDiagnosis secondaryDiagnosis);

	void setDiagnosisCertainty(EncounterDiagnosis confirmedDiagnosis);

	void removeDiagnosis(EncounterDiagnosis removeDiagnosis, String order);

	void setDiagnoses(List<ConceptSearchResult> concepts);
}
