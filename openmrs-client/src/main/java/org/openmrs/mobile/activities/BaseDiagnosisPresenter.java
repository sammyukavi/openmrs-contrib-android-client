package org.openmrs.mobile.activities;

import android.util.Log;

import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.ConceptSearchDataService;
import org.openmrs.mobile.data.impl.ObsDataService;
import org.openmrs.mobile.data.impl.VisitNoteDataService;
import org.openmrs.mobile.models.ConceptSearchResult;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.VisitNote;

import java.util.List;

public class BaseDiagnosisPresenter {

	private ConceptSearchDataService conceptSearchDataService;
	private ObsDataService obsDataService;
	private VisitNoteDataService visitNoteDataService;

	public BaseDiagnosisPresenter() {
		this.conceptSearchDataService = new ConceptSearchDataService();
		this.obsDataService = new ObsDataService();
		this.visitNoteDataService = new VisitNoteDataService();
	}

	public void findConcept(String searchQuery,  IBaseDiagnosisFragment base) {
		conceptSearchDataService.search(searchQuery, new DataService.GetCallback<List<ConceptSearchResult>>() {
			@Override
			public void onCompleted(List<ConceptSearchResult> entities) {
				if (entities.isEmpty()) {
					ConceptSearchResult nonCodedDiagnosis = new ConceptSearchResult();
					nonCodedDiagnosis.setDisplay(searchQuery);
					nonCodedDiagnosis.setValue("Non-Coded:" + searchQuery);
					entities.add(nonCodedDiagnosis);
				}
				base.getBaseDiagnosisView().setDiagnoses(entities);
			}

			@Override
			public void onError(Throwable t) {
				Log.e("error", t.getLocalizedMessage());
			}
		});
	}

	public void getObservation(String uuid, IBaseDiagnosisFragment base) {
		obsDataService.getByUUID(uuid, QueryOptions.LOAD_RELATED_OBJECTS, new DataService.GetCallback<Observation>() {
			@Override
			public void onCompleted(Observation entity) {
				base.getBaseDiagnosisView().createEncounterDiagnosis(entity, entity.getDisplay(), entity.getValueCodedName());
			}

			@Override
			public void onError(Throwable t) {
				base.getBaseDiagnosisView().showTabSpinner(false);
			}
		});
	}

	public void saveVisitNote(VisitNote visitNote, IBaseDiagnosisFragment base) {
		base.getBaseDiagnosisView().showTabSpinner(true);
		visitNoteDataService.save(visitNote, new DataService.GetCallback<VisitNote>() {
			@Override
			public void onCompleted(VisitNote visitNote) {
				base.getBaseDiagnosisView().showTabSpinner(false);
				base.setEncounterUuid(visitNote.getEncounterId());
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("FAILED:::" + t.getMessage());
			}
		});
	}
}
