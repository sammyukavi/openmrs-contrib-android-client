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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import org.openmrs.mobile.models.Patient;
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
	private LocalDate birthdate;
	private DateTime bdt;
	private EditText edfname;
	private EditText edmname;
	private EditText edlname;
	private EditText eddob;
	private EditText edyr;
	private EditText edmonth;
	private EditText edaddr1;
	private EditText edaddr2;
	private EditText edcity;
	private EditText fileNumber;
	private EditText county;
	private EditText subCounty;
	private EditText nationality;
	private EditText patientIdNo;
	private EditText clinic;
	private EditText ward;
	private EditText phonenumber;
	private EditText kinName;
	private EditText kinRelationship;
	private EditText kinPhonenumber;
	private EditText kinResidence;
	private EditText encounterDate;
	private EditText encounterDept;
	private EditText encounterProvider;
	private RadioGroup gen;
	private ProgressBar progressBar;

	private Button submitConfirm;
	private String[] countries;
	private ImageView patientImageView;
	private FloatingActionButton capturePhoto;
	private Bitmap patientPhoto = null;
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
	private TextView countyerror;
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

	public static AddEditPatientFragment newInstance() {
		return new AddEditPatientFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_patient_info, container, false);
		resolveViews(root);
		//addSuggestionsToAutoCompleTextView();
		addListeners();
		fillFields(mPresenter.getPatientToUpdate());
		return root;
	}

	@Override
	public void finishPatientInfoActivity() {
		getActivity().finish();
	}

	@Override
	public void scrollToTop() {
		ScrollView scrollView = (ScrollView)this.getActivity().findViewById(R.id.scrollView);
		scrollView.smoothScrollTo(0, scrollView.getPaddingTop());
	}

	@Override
	public void setErrorsVisibility(boolean givenNameError, boolean familyNameError, boolean dayOfBirthError,
			boolean addressError, boolean countryError, boolean genderError, boolean patientFileNumberError, boolean
			civilStatusError, boolean occuaptionerror) {
		if (givenNameError) {
			fnameerror.setVisibility(View.VISIBLE);
		} else {
			fnameerror.setVisibility(View.INVISIBLE);
		}

		if (familyNameError) {
			lnameerror.setVisibility(View.VISIBLE);
		} else {
			lnameerror.setVisibility(View.INVISIBLE);
		}

		if (dayOfBirthError) {
			doberror.setVisibility(View.VISIBLE);
		} else {
			doberror.setVisibility(View.GONE);
		}

		if (addressError) {
			addrerror.setVisibility(View.VISIBLE);
		} else {
			addrerror.setVisibility(View.GONE);
		}

		if (countryError) {
			countyerror.setVisibility(View.VISIBLE);
		} else {
			countyerror.setVisibility(View.GONE);
		}

		if (genderError) {
			gendererror.setVisibility(View.VISIBLE);
		} else {
			gendererror.setVisibility(View.GONE);
		}

		if (patientFileNumberError) {
			fileNumberError.setVisibility(View.VISIBLE);
		} else {
			fileNumberError.setVisibility(View.GONE);
		}

		if (civilStatusError) {
			marriageStatusError.setVisibility(View.VISIBLE);
		} else {
			marriageStatusError.setVisibility(View.GONE);
		}

		if (occuaptionerror) {
			occupationError.setVisibility(View.VISIBLE);
		} else {
			occupationError.setVisibility(View.GONE);
		}
	}

	private Person createPerson() {
		Person person = new Person();

		// Add address
		PersonAddress address = new PersonAddress();
		address.setAddress1(ViewUtils.getInput(edaddr1));
		address.setAddress2(ViewUtils.getInput(edaddr2));
		address.setCityVillage(ViewUtils.getInput(edcity));
		address.setPreferred(true);

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
				int yeardiff = ViewUtils.isEmpty(edyr) ? 0 : Integer.parseInt(edyr.getText().toString());
				int mondiff = ViewUtils.isEmpty(edmonth) ? 0 : Integer.parseInt(edmonth.getText().toString());
				LocalDate now = new LocalDate();
				bdt = now.toDateTimeAtStartOfDay().toDateTime();
				bdt = bdt.minusYears(yeardiff);
				bdt = bdt.minusMonths(mondiff);
				person.setBirthdateEstimated(true);
				birthdate = dateTimeFormatter.print(bdt);
			}
		} else {
			birthdate = dateTimeFormatter.print(bdt);
		}
		person.setBirthdate(birthdate);

		if (patientPhoto != null)
			person.setPhoto(patientPhoto);

		return person;
	}

	private Patient createPatient() {
		final Patient patient = new Patient();
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
		intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, patient.getId());
		startActivity(intent);
	}

	@Override
	public void showUpgradeRegistrationModuleInfo() {
		ToastUtil.notifyLong(getResources().getString(R.string.registration_core_info));
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
		fileNumber = (EditText)v.findViewById(R.id.fileNumber);
		occupation = (EditText)v.findViewById(R.id.occuapation);
		county = (EditText)v.findViewById(R.id.county);
		subCounty = (EditText)v.findViewById(R.id.sub_county);
		nationality = (EditText)v.findViewById(R.id.nationality);
		patientIdNo = (EditText)v.findViewById(R.id.patient_id_no);
		clinic = (EditText)v.findViewById(R.id.clinic);
		ward = (EditText)v.findViewById(R.id.ward);
		phonenumber = (EditText)v.findViewById(R.id.phonenumber);
		kinName = (EditText)v.findViewById(R.id.kinName);
		kinRelationship = (EditText)v.findViewById(R.id.kinRelationship);
		kinPhonenumber = (EditText)v.findViewById(R.id.kinPhonenumber);
		kinResidence = (EditText)v.findViewById(R.id.kinResidence);
		encounterDate = (EditText)v.findViewById(R.id.encounterDate);
		encounterDept = (EditText)v.findViewById(R.id.encounterDept);
		encounterProvider = (EditText)v.findViewById(R.id.encounterProvider);

		gen = (RadioGroup)v.findViewById(R.id.gender);
		progressBar = (ProgressBar)v.findViewById(R.id.progress_bar);

		fnameerror = (TextView)v.findViewById(R.id.fnameerror);
		lnameerror = (TextView)v.findViewById(R.id.lnameerror);
		doberror = (TextView)v.findViewById(R.id.doberror);
		gendererror = (TextView)v.findViewById(R.id.gendererror);
		addrerror = (TextView)v.findViewById(R.id.addrerror);
		fileNumberError = (TextView)v.findViewById(R.id.fileNumberError);
		marriageStatusError = (TextView)v.findViewById(R.id.civilStatusError);
		countyerror = (TextView)v.findViewById(R.id.countyError);
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
		encounterDateError = (TextView)v.findViewById(R.id.encounterDateError);
		encounterDeptError = (TextView)v.findViewById(R.id.encounterDeptError);
		encounterProviderError = (TextView)v.findViewById(R.id.encounterProviderError);
		occupationError = (TextView)v.findViewById(R.id.occupationError);

		submitConfirm = (Button)v.findViewById(R.id.submitConfirm);
		civilStatus = (Spinner)v.findViewById(R.id.civilStatusSpinner);
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

			edaddr1.setText(person.getAddress().getAddress1());
			edaddr2.setText(person.getAddress().getAddress2());
			edcity.setText(person.getAddress().getCityVillage());

		}
	}

	/*private void addSuggestionsToAutoCompleTextView() {
		countries = getContext().getResources().getStringArray(R.array.countries_array);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
				android.R.layout.simple_dropdown_item_1line, countries);
		edcountry.setAdapter(adapter);

	}

	private void addSuggestionsToCities() {
		String country_name = edcountry.getText().toString();
		country_name = country_name.replace("(", "");
		country_name = country_name.replace(")", "");
		country_name = country_name.replace(" ", "");
		country_name = country_name.replace("-", "_");
		country_name = country_name.replace(".", "");
		country_name = country_name.replace("'", "");
		int resourceId =
				this.getResources().getIdentifier(country_name.toLowerCase(), "array", getContext().getPackageName());
		if (resourceId != 0) {
			String[] states = getContext().getResources().getStringArray(resourceId);
			ArrayAdapter<String> state_adapter = new ArrayAdapter<>(getContext(),
					android.R.layout.simple_dropdown_item_1line, states);
			edstate.setAdapter(state_adapter);
		}
	}*/

	private void addListeners() {
		gen.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
				gendererror.setVisibility(View.GONE);
			}
		});

		/*edcountry.setThreshold(2);
		edcountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (edcountry.getText().length() >= edcountry.getThreshold()) {
					edcountry.showDropDown();
				}
				if (Arrays.asList(countries).contains(edcountry.getText().toString())) {
					edcountry.dismissDropDown();
				}
			}
		});
		edstate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				addSuggestionsToCities();
			}
		});*/
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
