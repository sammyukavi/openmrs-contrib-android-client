package org.openmrs.mobile.activities.patientheader;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.models.Patient;

public class PatientHeaderPresenter extends BasePresenter implements PatientHeaderContract.Presenter {

	private PatientDataService patientDataService;
	private PatientHeaderContract.View view;
	private String patientUuid;

	public PatientHeaderPresenter(PatientHeaderContract.View view, String patientUuid) {
		this.view = view;
		this.view.setPresenter(this);
		this.patientUuid = patientUuid;
		this.patientDataService = new PatientDataService();
	}

	@Override
	public void getPatient() {
		patientDataService.getByUUID(patientUuid, QueryOptions.LOAD_RELATED_OBJECTS, new DataService.GetCallback<Patient>
				() {
			@Override
			public void onCompleted(Patient patient) {
				if (patient != null) {
					view.updatePatientHeader(patient);
				}
			}

			@Override
			public void onError(Throwable t) {
				//ToastUtil.error(t.getMessage());'
				t.printStackTrace();
			}
		});
	}

	@Override
	public void subscribe() {
		getPatient();
	}
}
