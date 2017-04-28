/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities.addeditpatient;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.listeners.watcher.PatientBirthdateValidatorWatcher;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientIdentifier;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.PersonAddress;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;
import org.openmrs.mobile.utilities.ViewUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddEditPatientFragment extends ACBaseFragment<AddEditPatientContract.Presenter>
		implements AddEditPatientContract.View {

	private final static int IMAGE_REQUEST = 1;
	private LinearLayout linearLayout;
	private LocalDate birthdate, patientEncouterDate;
	private DateTime bdt, enconterdate;
	private EditText edfname;
	private EditText edmname;
	private EditText edlname;
	private EditText eddob;
	private EditText edyr;
	private EditText edmonth;
	private EditText fileNumber;
	private AutoCompleteTextView county;
	private AutoCompleteTextView subCounty;
	private EditText nationality;
	private EditText patientIdNo;
	private EditText clinic;
	private EditText ward;
	private EditText phonenumber;
	private EditText kinName;
	private EditText kinRelationship;
	private EditText kinPhonenumber;
	private EditText kinResidence;
	private RadioGroup gen;
	private ProgressBar progressBar;

	private Button submitConfirm;
	private String[] counties;
	private ImageView patientImageView;
	private String patientName;
	private File output = null;
	private OpenMRSLogger logger = new OpenMRSLogger();
	private Spinner civilStatus;
	private EditText occupation;

	/*
	*TextViews defination
	 *  */
	private TextView fnameerror;
	private TextView lnameerror;
	private TextView doberror;
	private TextView gendererror;
	private TextView addrerror;
	private TextView fileNumberError;
	private TextView marriageStatusError;
	private TextView occupationError;
	private TextView countyError;
	private TextView subCountyError;
	private TextView nationalityError;
	private TextView patientIdNoError;
	private TextView clinicError;
	private TextView wardError;
	private TextView phonenumberError;
	private TextView kinNameError;
	private TextView kinRelationshipError;
	private TextView kinPhonenumberError;
	private TextView kinResidenceError;
	private TextView encounterDateError;
	private TextView encounterDeptError;
	private TextView encounterProviderError;
	private String[] patientCivilStatus;
	Concept test;

	public static AddEditPatientFragment newInstance() {
		return new AddEditPatientFragment();
	}

	private void resolveViews(View v) {
		linearLayout = (LinearLayout)v.findViewById(R.id.addEditLinearLayout);
		edfname = (EditText)v.findViewById(R.id.firstname);
		edmname = (EditText)v.findViewById(R.id.middlename);
		edlname = (EditText)v.findViewById(R.id.surname);
		eddob = (EditText)v.findViewById(R.id.dob);
		edyr = (EditText)v.findViewById(R.id.estyr);
		edmonth = (EditText)v.findViewById(R.id.estmonth);
		fileNumber = (EditText)v.findViewById(R.id.fileNumber);
		occupation = (EditText)v.findViewById(R.id.occuapation);
		county = (AutoCompleteTextView)v.findViewById(R.id.county);
		subCounty = (AutoCompleteTextView)v.findViewById(R.id.sub_county);
		nationality = (EditText)v.findViewById(R.id.nationality);
		patientIdNo = (EditText)v.findViewById(R.id.patient_id_no);
		clinic = (EditText)v.findViewById(R.id.clinic);
		ward = (EditText)v.findViewById(R.id.ward);
		phonenumber = (EditText)v.findViewById(R.id.phonenumber);
		kinName = (EditText)v.findViewById(R.id.kinName);
		kinRelationship = (EditText)v.findViewById(R.id.kinRelationship);
		kinPhonenumber = (EditText)v.findViewById(R.id.kinPhonenumber);
		kinResidence = (EditText)v.findViewById(R.id.kinResidence);

		gen = (RadioGroup)v.findViewById(R.id.gender);
		progressBar = (ProgressBar)v.findViewById(R.id.progress_bar);

		fnameerror = (TextView)v.findViewById(R.id.fnameerror);
		lnameerror = (TextView)v.findViewById(R.id.lnameerror);
		doberror = (TextView)v.findViewById(R.id.doberror);
		gendererror = (TextView)v.findViewById(R.id.gendererror);
		addrerror = (TextView)v.findViewById(R.id.addrerror);
		fileNumberError = (TextView)v.findViewById(R.id.fileNumberError);
		marriageStatusError = (TextView)v.findViewById(R.id.civilStatusError);
		countyError = (TextView)v.findViewById(R.id.countyError);
		subCountyError = (TextView)v.findViewById(R.id.sub_countError);
		nationalityError = (TextView)v.findViewById(R.id.nationalityError);
		patientIdNoError = (TextView)v.findViewById(R.id.patient_id_noError);
		clinicError = (TextView)v.findViewById(R.id.clinicError);
		wardError = (TextView)v.findViewById(R.id.wardError);
		phonenumberError = (TextView)v.findViewById(R.id.phonenumberError);
		kinNameError = (TextView)v.findViewById(R.id.kinNameError);
		kinRelationshipError = (TextView)v.findViewById(R.id.kinRelationshipError);
		kinPhonenumberError = (TextView)v.findViewById(R.id.kinPhonenumberError);
		kinResidenceError = (TextView)v.findViewById(R.id.kinResidenceError);
		occupationError = (TextView)v.findViewById(R.id.occupationError);

		submitConfirm = (Button)v.findViewById(R.id.submitConfirm);
		civilStatus = (Spinner)v.findViewById(R.id.civilStatusSpinner);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_patient_info, container, false);
		resolveViews(root);
		addSuggestionsToAutoCompleteTextView();
		addListeners();
		fillFields(mPresenter.getPatientToUpdate());
		System.out.print(test);
		return root;

	}

	@Override
	public void finishAddPatientActivity() {
		getActivity().finish();
	}

	@Override
	public void scrollToTop() {
		ScrollView scrollView = (ScrollView)this.getActivity().findViewById(R.id.scrollView);
		scrollView.smoothScrollTo(0, scrollView.getPaddingTop());
	}

	@Override
	public void setErrorsVisibility(
			boolean givenNameError, boolean familyNameError, boolean dayOfBirthError, boolean addressError,
			boolean county_Error, boolean genderError, boolean patientFileNumberError, boolean civilStatusError,
			boolean occuaptionerror, boolean subCounty_Error, boolean nationality_Error, boolean patientIdNo_Error,
			boolean clinic_Error, boolean ward_Error, boolean phonenumber_Error, boolean kinName_Error,
			boolean kinRelationship_Error, boolean kinPhonenumber_Error, boolean kinResidence_Error
	) {
		fnameerror.setVisibility(givenNameError ? View.VISIBLE : View.INVISIBLE);
		lnameerror.setVisibility(familyNameError ? View.VISIBLE : View.INVISIBLE);
		doberror.setVisibility(dayOfBirthError ? View.VISIBLE : View.GONE);
		addrerror.setVisibility(addressError ? View.VISIBLE : View.GONE);
		countyError.setVisibility(county_Error ? View.VISIBLE : View.GONE);
		gendererror.setVisibility(genderError ? View.VISIBLE : View.GONE);
		fileNumberError.setVisibility(patientFileNumberError ? View.VISIBLE : View.GONE);
		marriageStatusError.setVisibility(civilStatusError ? View.VISIBLE : View.GONE);
		occupationError.setVisibility(occuaptionerror ? View.VISIBLE : View.GONE);
		subCountyError.setVisibility(subCounty_Error ? View.VISIBLE : View.GONE);
		nationalityError.setVisibility(nationality_Error ? View.VISIBLE : View.GONE);
		patientIdNoError.setVisibility(patientIdNo_Error ? View.VISIBLE : View.GONE);
		clinicError.setVisibility(clinic_Error ? View.VISIBLE : View.GONE);
		wardError.setVisibility(ward_Error ? View.VISIBLE : View.GONE);
		phonenumberError.setVisibility(phonenumber_Error ? View.VISIBLE : View.GONE);
		kinNameError.setVisibility(kinName_Error ? View.VISIBLE : View.GONE);
		kinRelationshipError.setVisibility(kinRelationship_Error ? View.VISIBLE : View.GONE);
		kinPhonenumberError.setVisibility(kinPhonenumber_Error ? View.VISIBLE : View.GONE);
		kinResidenceError.setVisibility(kinResidence_Error ? View.VISIBLE : View.GONE);

	}

	private Person createPerson() {
		Person person = new Person();

		// Add address
		PersonAddress address = new PersonAddress();
		address.getPreferred();

		List<PersonAddress> addresses = new ArrayList<>();
		addresses.add(address);
		person.setAddresses(addresses);

		// Add names
		PersonName name = new PersonName();
		name.setFamilyName(ViewUtils.getInput(edlname));
		name.setGivenName(ViewUtils.getInput(edfname));
		name.setMiddleName(ViewUtils.getInput(edmname));

		List<PersonName> names = new ArrayList<>();
		names.add(name);
		person.setNames(names);

		// Add gender
		String[] genderChoices = { "M", "F" };
		int index = gen.indexOfChild(getActivity().findViewById(gen.getCheckedRadioButtonId()));
		if (index != -1) {
			person.setGender(genderChoices[index]);
		} else {
			person.setGender(null);
		}

		// Add birthdate
		String birthdate = null;
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT);
		if (ViewUtils.isEmpty(eddob)) {
			if (!StringUtils.isBlank(ViewUtils.getInput(edyr)) || !StringUtils.isBlank(ViewUtils.getInput(edmonth))) {
				int yearDiff = ViewUtils.isEmpty(edyr) ? 0 : Integer.parseInt(edyr.getText().toString());
				int monthDiff = ViewUtils.isEmpty(edmonth) ? 0 : Integer.parseInt(edmonth.getText().toString());
				LocalDate now = new LocalDate();
				bdt = now.toDateTimeAtStartOfDay().toDateTime();
				bdt = bdt.minusYears(yearDiff);
				bdt = bdt.minusMonths(monthDiff);
				person.setBirthdateEstimated(true);
				birthdate = dateTimeFormatter.print(bdt);
			}
		} else {
			birthdate = dateTimeFormatter.print(bdt);
		}
		person.setBirthdate(birthdate);

		return person;
	}

	private Patient createPatient() {
		final Patient patient = new Patient();

		// Add identifier
		PatientIdentifier identifier = new PatientIdentifier();
		identifier.setIdentifier(ViewUtils.getInput(fileNumber));

		List<PatientIdentifier> patientIdentifierList = new ArrayList<>();
		patientIdentifierList.add(identifier);
		patient.setIdentifiers(patientIdentifierList);

		patient.setPerson(createPerson());
		patient.setUuid(" ");
		return patient;
	}

	private Patient updatePatient(Patient patient) {
		patient.setPerson(createPerson());
		return patient;
	}

	@Override
	public void hideSoftKeys() {
		View view = this.getActivity().getCurrentFocus();
		if (view == null) {
			view = new View(this.getActivity());
		}
		InputMethodManager inputMethodManager =
				(InputMethodManager)this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	@Override
	public void setProgressBarVisibility(boolean visibility) {
		progressBar.setVisibility(visibility ? View.VISIBLE : View.GONE);
	}

	@Override
	public void showSimilarPatientDialog(List<Patient> patients, Patient newPatient) {
		setProgressBarVisibility(false);
		CustomDialogBundle similarPatientsDialog = new CustomDialogBundle();
		similarPatientsDialog.setTitleViewMessage(getString(R.string.similar_patients_dialog_title));
		similarPatientsDialog.setRightButtonText(getString(R.string.dialog_button_register_new));
		similarPatientsDialog.setRightButtonAction(CustomFragmentDialog.OnClickAction.REGISTER_PATIENT);
		similarPatientsDialog.setLeftButtonText(getString(R.string.dialog_button_cancel));
		similarPatientsDialog.setLeftButtonAction(CustomFragmentDialog.OnClickAction.CANCEL_REGISTERING);
		similarPatientsDialog.setPatientsList(patients);
		similarPatientsDialog.setNewPatient(newPatient);
		((AddEditPatientActivity)this.getActivity())
				.createAndShowDialog(similarPatientsDialog, ApplicationConstants.DialogTAG.SIMILAR_PATIENTS_TAG);
	}

	@Override
	public void startPatientDashboardActivity(Patient patient) {
		Intent intent = new Intent(getActivity(), PatientDashboardActivity.class);
		intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patient.getId());
		startActivity(intent);
	}

	@Override
	public void showUpgradeRegistrationModuleInfo() {
		ToastUtil.notifyLong(getResources().getString(R.string.registration_core_info));
	}

	@Override
	public void setCivilStatus(Concept civilStatus) {
		test = civilStatus;
	}

	private void fillFields(final Patient patient) {
		if (patient != null && patient.getPerson() != null) {
			//Change to Update Patient Form
			String updatePatientStr = getResources().getString(R.string.action_update_patient_data);
			this.getActivity().setTitle(updatePatientStr);
			submitConfirm.setText(updatePatientStr);
			submitConfirm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mPresenter.confirmUpdate(updatePatient(patient));
				}
			});

			Person person = patient.getPerson();
			edfname.setText(person.getName().getGivenName());
			edmname.setText(person.getName().getMiddleName());
			edlname.setText(person.getName().getFamilyName());

			patientName = person.getName().getNameString();

			if (StringUtils.notNull(person.getBirthdate()) || StringUtils.notEmpty(person.getBirthdate())) {
				bdt = DateUtils.convertTimeString(person.getBirthdate());
				eddob.setText(DateUtils.convertTime(DateUtils.convertTime(bdt.toString(), DateUtils
								.OPEN_MRS_REQUEST_FORMAT),
						DateUtils.DEFAULT_DATE_FORMAT));
			}

			if (("M").equals(person.getGender())) {
				gen.check(R.id.male);
			} else if (("F").equals(person.getGender())) {
				gen.check(R.id.female);
			}

		}
	}

	private void addSuggestionsToAutoCompleteTextView() {
		counties = getContext().getResources().getStringArray(R.array.countiesArray);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
				android.R.layout.simple_dropdown_item_1line, counties);
		county.setAdapter(adapter);

	}

	private void addSuggestionsToSubCounties() {
		String countyName = county.getText().toString();
		countyName = countyName.replace("(", "");
		countyName = countyName.replace(")", "");
		countyName = countyName.replace(" ", "");
		countyName = countyName.replace("-", "_");
		countyName = countyName.replace(".", "");
		countyName = countyName.replace("'", "");
		int resourceId =
				this.getResources().getIdentifier(countyName.toLowerCase(), "array", getContext().getPackageName());
		if (resourceId != 0) {
			String[] subCounties = getContext().getResources().getStringArray(resourceId);
			ArrayAdapter<String> countiesAdapter = new ArrayAdapter<>(getContext(),
					android.R.layout.simple_dropdown_item_1line, subCounties);
			subCounty.setAdapter(countiesAdapter);
		}
	}

	private void addListeners() {

		/*civilStatus.setAdapter(new ArrayAdapter<String>(AddEditPatientFragment.this, android.R.layout
				.simple_spinner_dropdown_item));*/

		gen.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
				gendererror.setVisibility(View.GONE);
			}
		});

		county.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (county.getText().length() >= county.getThreshold()) {
					county.showDropDown();
				}
				if (Arrays.asList(counties).contains(county.getText().toString())) {
					county.dismissDropDown();
				}
			}
		});

		county.setThreshold(2);
		subCounty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				addSuggestionsToSubCounties();
			}
		});

		if (eddob != null) {
			eddob.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int cYear;
					int cMonth;
					int cDay;

					if (bdt == null) {
						Calendar currentDate = Calendar.getInstance();
						cYear = currentDate.get(Calendar.YEAR);
						cMonth = currentDate.get(Calendar.MONTH);
						cDay = currentDate.get(Calendar.DAY_OF_MONTH);
					} else {
						cYear = bdt.getYear();
						cMonth = bdt.getMonthOfYear() - 1;
						cDay = bdt.getDayOfMonth();
					}

					edmonth.getText().clear();
					edyr.getText().clear();

					DatePickerDialog mDatePicker = new DatePickerDialog(AddEditPatientFragment.this.getActivity(),
							new DatePickerDialog.OnDateSetListener() {
								public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth,
										int selectedday) {
									int adjustedMonth = selectedmonth + 1;
									eddob.setText(selectedday + "/" + adjustedMonth + "/" + selectedyear);
									birthdate = new LocalDate(selectedyear, adjustedMonth, selectedday);
									bdt = birthdate.toDateTimeAtStartOfDay().toDateTime();
								}
							}, cYear, cMonth, cDay);
					mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
					mDatePicker.setTitle("Select Date");
					mDatePicker.show();
				}
			});
		}

		submitConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mPresenter.confirmRegister(createPatient());
			}
		});

		TextWatcher textWatcher = new PatientBirthdateValidatorWatcher(eddob, edmonth, edyr);
		edmonth.addTextChangedListener(textWatcher);
		edyr.addTextChangedListener(textWatcher);
	}

	private Snackbar createSnackbarLong(int stringId) {
		Snackbar snackbar = Snackbar.make(linearLayout, stringId, Snackbar.LENGTH_LONG);
		View sbView = snackbar.getView();
		TextView textView = (TextView)sbView.findViewById(android.support.design.R.id.snackbar_text);
		textView.setTextColor(Color.WHITE);
		return snackbar;
	}

}
