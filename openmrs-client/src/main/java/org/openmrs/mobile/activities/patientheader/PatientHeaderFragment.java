package org.openmrs.mobile.activities.patientheader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.DateUtils;

public class PatientHeaderFragment extends ACBaseFragment<PatientHeaderContract.Presenter>
		implements PatientHeaderContract.View{

	private TextView patientDisplayName, patientGender, patientAge, patientIdentifier,
			patientDob;

	public static PatientHeaderFragment newInstance(){
		return new PatientHeaderFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_patient_header, container, false);
		patientDisplayName = (TextView)fragmentView.findViewById(R.id.fetchedPatientDisplayName);
		patientIdentifier = (TextView)fragmentView.findViewById(R.id.fetchedPatientIdentifier);
		patientGender = (TextView)fragmentView.findViewById(R.id.fetchedPatientGender);
		patientAge = (TextView)fragmentView.findViewById(R.id.fetchedPatientAge);
		patientDob = (TextView)fragmentView.findViewById(R.id.fetchedPatientBirthDate);

		return fragmentView;
	}

	@Override
	public void updatePatientHeader(Patient patient) {
		System.out.println("UPDATE::" + patient);
		patientDisplayName.setText(patient.getPerson().getName().getNameString());
		patientGender.setText(patient.getPerson().getGender());
		patientIdentifier.setText(patient.getIdentifier().getIdentifier());
		DateTime date = DateUtils.convertTimeString(patient.getPerson().getBirthdate());
		patientAge.setText(DateUtils.calculateAge(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth()));
		patientDob.setText(DateUtils.convertTime1(patient.getPerson().getBirthdate(), DateUtils.PATIENT_DASHBOARD_DOB_DATE_FORMAT));
	}
}
