package org.openmrs.mobile.activities.patientheader;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.event.DataRefreshEvent;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;

public class PatientHeaderPresenter extends BasePresenter implements PatientHeaderContract.Presenter {

	private PatientDataService patientDataService;
	private PatientHeaderContract.View patientHeaderView;
	private String patientUuid;

	private Timer syncAgeUpdateTimer;
	private TimerTask syncAgeUpdateTimerTask;
	private Patient currentPatient;
	private Date currentPatientLastSyncTime;

	// one minute
	private final int TIMER_DELAY_IN_MS = 60000;

	public PatientHeaderPresenter(PatientHeaderContract.View view, String patientUuid) {
		this.patientHeaderView = view;
		this.patientHeaderView.setPresenter(this);
		this.patientUuid = patientUuid;

		this.patientDataService = dataAccess().patient();
	}

	@Override
	public void getPatient() {
		// This method gets called each time the screen turns on, so we don't want to update the sync display if the data
		// hasn't truly refreshed on the screen
		final boolean isPatientNewMeaningLastSyncTimeShouldBeRefreshed = (currentPatient == null);
		patientHeaderView.holdHeader(true);
		patientDataService.getByUuid(patientUuid, QueryOptions.FULL_REP,
				new DataService.GetCallback<Patient>() {
			@Override
			public void onCompleted(Patient patient) {
				if (patient != null) {
					currentPatient = patient;
					patientHeaderView.holdHeader(false);
					patientHeaderView.updatePatientHeader(patient);
					if (isPatientNewMeaningLastSyncTimeShouldBeRefreshed) {
						currentPatientLastSyncTime = patientDataService.getLastSyncTime(currentPatient);
					}
				}

				updatePatientSyncAge();
			}

			@Override
			public void onError(Throwable t) {
				patientHeaderView.holdHeader(false);
				t.printStackTrace();
			}
		});
	}

	@Override
	public void dataRefreshEventOccurred(DataRefreshEvent dataRefreshEvent) {
		if (dataRefreshEvent.getMessage().equalsIgnoreCase(ApplicationConstants.EventMessages.DataRefresh.DATA_RETRIEVED)) {
			refreshPatientLastSyncTime();
		}
	}

	private void refreshPatientLastSyncTime() {
		if (currentPatient != null && currentPatientLastSyncTime == null) {
			currentPatientLastSyncTime = patientDataService.getLastSyncTime(currentPatient);
			updatePatientSyncAge();
		}
	}

	private void updatePatientSyncAge() {
		String lastSyncCalendarTimeDifference = ApplicationConstants.EMPTY_STRING;
		if (currentPatientLastSyncTime != null) {
			lastSyncCalendarTimeDifference = DateUtils.calculateTimeDifference(currentPatientLastSyncTime, false);
		}
		final String lastSyncCalendarTimeDifferenceToUse = lastSyncCalendarTimeDifference.toLowerCase();
		patientHeaderView.runOnUIThread(() -> patientHeaderView.updatePatientSyncAge(lastSyncCalendarTimeDifferenceToUse));
	}

	@Override
	public void subscribe() {
		getPatient();
		createPatientSyncAgeTimer();
	}

	@Override
	public void unsubscribe() {
		destroyPatientSyncAgeTimer();
	}

	private void destroyPatientSyncAgeTimer() {
		syncAgeUpdateTimerTask.cancel();
	}

	private void createPatientSyncAgeTimer() {
		if (syncAgeUpdateTimer == null) {
			syncAgeUpdateTimer = new Timer();
		}
		syncAgeUpdateTimerTask = new TimerTask() {

			@Override
			public void run() {
				updatePatientSyncAge();
			}
		};
		syncAgeUpdateTimer.schedule(syncAgeUpdateTimerTask, TIMER_DELAY_IN_MS, TIMER_DELAY_IN_MS);
	}
}
