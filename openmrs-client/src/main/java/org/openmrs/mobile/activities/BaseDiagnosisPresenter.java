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

	public void findConcept(String searchQuery, IBaseDiagnosisView view) {
		conceptSearchDataService.search(searchQuery, new DataService.GetCallback<List<ConceptSearchResult>>() {
			@Override
			public void onCompleted(List<ConceptSearchResult> entities) {
				if (entities.isEmpty()) {
					ConceptSearchResult nonCodedDiagnosis = new ConceptSearchResult();
					nonCodedDiagnosis.setDisplay(searchQuery);
					nonCodedDiagnosis.setValue("Non-Coded:" + searchQuery);
					entities.add(nonCodedDiagnosis);
				}
				view.setDiagnoses(entities);
			}

			@Override
			public void onError(Throwable t) {
				Log.e("error", t.getLocalizedMessage());
			}
		});
	}

	public void getObservation(String uuid,  IBaseDiagnosisView view) {
		obsDataService.getByUUID(uuid, QueryOptions.LOAD_RELATED_OBJECTS, new DataService.GetCallback<Observation>() {
			@Override
			public void onCompleted(Observation entity) {
				view.createEncounterDiagnosis(entity, entity.getDisplay(), entity.getValueCodedName());
			}

			@Override
			public void onError(Throwable t) {
				//view.showTabSpinner(false);
			}
		});
	}

	public void saveVisitNote(VisitNote visitNote, IBaseDiagnosisView view) {
		//view.showTabSpinner(true);
		visitNoteDataService.save(visitNote, new DataService.GetCallback<VisitNote>() {
			@Override
			public void onCompleted(VisitNote visitNote) {
				//view.showTabSpinner(false);
				System.out.println("RETURNED:::" + visitNote);
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("FAILED:::" + t.getMessage());
			}
		});
	}
}
