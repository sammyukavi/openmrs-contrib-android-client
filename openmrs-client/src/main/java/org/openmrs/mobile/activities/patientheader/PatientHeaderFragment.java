package org.openmrs.mobile.activities.patientheader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PersonAttribute;
import org.openmrs.mobile.utilities.DateUtils;

import static org.openmrs.mobile.utilities.ApplicationConstants.visitAttributeTypes.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER;

public class PatientHeaderFragment extends ACBaseFragment<PatientHeaderContract.Presenter>
		implements PatientHeaderContract.View {

	private TextView patientDisplayName, patientAge, fileNumber, patientDob, patientAddress, patientPhonenumber;
	ImageView patientGender;

	public static PatientHeaderFragment newInstance() {
		return new PatientHeaderFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_patient_header, container, false);
		patientDisplayName = (TextView)fragmentView.findViewById(R.id.fetchedPatientDisplayName);
		fileNumber = (TextView)fragmentView.findViewById(R.id.fileNumber);
		patientGender = (ImageView)fragmentView.findViewById(R.id.fetchedPatientGender);
		patientAge = (TextView)fragmentView.findViewById(R.id.fetchedPatientAge);
		patientDob = (TextView)fragmentView.findViewById(R.id.fetchedPatientBirthDate);
		patientAddress = (TextView)fragmentView.findViewById(R.id.patientAddres);
		patientPhonenumber = (TextView)fragmentView.findViewById(R.id.patientPhonenumber);

		return fragmentView;
	}

	@Override
	public void updatePatientHeader(Patient patient) {
		patientDisplayName.setText(patient.getPerson().getName().getNameString());
		patientGender.setImageResource(patient.getPerson().getGender().equalsIgnoreCase("f") ? R.drawable.female : R
				.drawable.male);
		fileNumber.setText(patient.getIdentifier().getIdentifier());
		DateTime date = DateUtils.convertTimeString(patient.getPerson().getBirthdate());
		patientAge.setText(DateUtils.calculateAge(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth()));
		patientDob.setText(
				DateUtils.convertTime1(patient.getPerson().getBirthdate(), DateUtils.PATIENT_DASHBOARD_DOB_DATE_FORMAT));

		String pnumber = "";
		for (PersonAttribute attribute : patient.getPerson().getAttributes()) {
			if (attribute.getAttributeType().getUuid().equalsIgnoreCase(PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER)) {
				pnumber = attribute.getValue().toString();
			}
		}
		patientPhonenumber.setText(pnumber);
		System.out.println(patient.getPerson());
		if (!patient.getPerson().getAddress().getAddress1().equals(null)) {
			patientAddress.setText(patient.getPerson().getAddress().getAddress1());
		}
	}

}
