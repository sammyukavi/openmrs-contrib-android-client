package org.openmrs.mobile.data.sync.impl;

import android.content.Context;

import org.openmrs.mobile.R;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.impl.ObsRestServiceImpl;
import org.openmrs.mobile.models.Observation;

import javax.inject.Inject;

/**
 * Sync Patient Summary (clinical note). The logic will compare local changes with what is stored remotely.
 * It will be upto the next user accessing the patient summary to keep/discard what is/not required!
 */
public class PatientSummarySyncService {

	private ObsRestServiceImpl obsRestService;
	private Context context;

	@Inject
	public PatientSummarySyncService(ObsRestServiceImpl obsRestService, Context context) {
		this.obsRestService = obsRestService;
		this.context = context;
	}

	/**
	 * Sync clinical note/patient summary. Observations don't have dateChanged, instead every change leads to the current
	 * observation being voided and a new one created. Check if the current observation has been voided, and if so, merge
	 * the patient summary else update with the current patient summary info.
	 * @param currentObservation
	 */
	public void sync(Observation currentObservation) {
		// Retrieve the actual observation on the server
		Observation existingObservation = RestHelper.getCallValue(obsRestService.getByEncounter(currentObservation
				.getEncounter().getUuid(), new QueryOptions.Builder()
				.customRepresentation(RestConstants.Representations.OBSERVATION)
				.build(), true));

		if (existingObservation != null) {

		} else {

		}

		if (currentObservation.getUuid().equalsIgnoreCase(existingObservation.getUuid())) {
			// no merging required
			RestHelper.getCallValue(obsRestService.update(existingObservation));
		} else {

		}

		String existingPatientSummary = context.getString(R.string.merge_clinical_note_template,
				existingObservation.getDiagnosisNote(),
				existingObservation.getCreator().getDisplay(),
				existingObservation.getDateCreated());
		String updatedPatientSummary = currentObservation.getDiagnosisNote();

		// void current observation
		existingObservation.setVoided(true);
		RestHelper.getCallValue(obsRestService.update(existingObservation));

		// create new observation with merged patient summary
		Observation updatedObservation = existingObservation.createCopyOfObservation();
		updatedObservation.setValue(existingPatientSummary + "\r\n" + updatedPatientSummary);

		// create new observation
		RestHelper.getCallValue(obsRestService.create(updatedObservation));
	}
}
