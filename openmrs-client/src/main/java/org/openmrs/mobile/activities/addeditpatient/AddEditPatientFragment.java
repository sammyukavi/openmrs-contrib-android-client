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
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.listeners.watcher.PatientBirthdateValidatorWatcher;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.models.ConceptName;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientIdentifier;
import org.openmrs.mobile.models.PatientIdentifierType;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.PersonAddress;
import org.openmrs.mobile.models.PersonAttribute;
import org.openmrs.mobile.models.PersonAttributeType;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;
import org.openmrs.mobile.utilities.ViewUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEditPatientFragment extends ACBaseFragment<AddEditPatientContract.Presenter>
		implements AddEditPatientContract.View {

	private final static int IMAGE_REQUEST = 1;
	private static LinearLayout.LayoutParams marginParams;
	private LinearLayout linearLayout;
	private LocalDate birthdate, patientEncouterDate;
	private DateTime bdt;
	private EditText edfname;
	private EditText edmname;
	private EditText edlname;
	private EditText eddob;
	private EditText edyr;
	private EditText edmonth;
	private EditText fileNumber;
	private RadioGroup gen;
	private ProgressBar progressBar;
	private Button submitConfirm;
	private String[] counties;
	private String patientUuuid;
	private ImageView patientImageView;
	private String patientName;
	private File output = null;
	private OpenMRSLogger logger = new OpenMRSLogger();
	/*
	*TextViews defination
	 *  */
	private TextView fnameerror;
	private TextView lnameerror;
	private TextView doberror;
	private TextView gendererror;
	private TextView addrerror;
	private TextView fileNumberError;
	private PatientIdentifierType patientIdentifierType;
	private Map<String, PersonAttribute> personAttributeMap = new HashMap<>();
	private Map<View, PersonAttributeType> viewPersonAttributeTypeMap = new HashMap<>();
	private LinearLayout personLinearLayout;
	private Location loginLocation;
	private OpenMRS instance = OpenMRS.getInstance();

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

		personLinearLayout = (LinearLayout)v.findViewById(R.id.personAttributeLinearLayout);

		gen = (RadioGroup)v.findViewById(R.id.gender);
		progressBar = (ProgressBar)v.findViewById(R.id.progress_bar);

		fnameerror = (TextView)v.findViewById(R.id.fnameerror);
		lnameerror = (TextView)v.findViewById(R.id.lnameerror);
		doberror = (TextView)v.findViewById(R.id.doberror);
		gendererror = (TextView)v.findViewById(R.id.gendererror);
		fileNumberError = (TextView)v.findViewById(R.id.fileNumberError);

		submitConfirm = (Button)v.findViewById(R.id.submitConfirm);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_patient_info, container, false);
		if (getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE) != null) {
			patientUuuid = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
		} else {
			patientUuuid = ApplicationConstants.EMPTY_STRING;
		}

		resolveViews(root);
		addListeners();
		buildMarginLayout();

		mPresenter.getPatientIdentifierTypes();
		mPresenter.getLoginLocation();

		if (!patientUuuid.isEmpty()) {
			mPresenter.getPatientToUpdate(instance.getPatientUuid());
		} else {
			mPresenter.getPersonAttributeTypes();
		}

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
			boolean givenNameError, boolean familyNameError, boolean dayOfBirthError,
			boolean county_Error, boolean genderError, boolean patientFileNumberError, boolean civilStatusError,
			boolean occuaptionerror, boolean subCounty_Error, boolean nationality_Error, boolean patientIdNo_Error,
			boolean clinic_Error, boolean ward_Error, boolean phonenumber_Error, boolean kinName_Error,
			boolean kinRelationship_Error, boolean kinPhonenumber_Error, boolean kinResidence_Error
	) {
		fnameerror.setVisibility(givenNameError ? View.VISIBLE : View.INVISIBLE);
		lnameerror.setVisibility(familyNameError ? View.VISIBLE : View.INVISIBLE);
		doberror.setVisibility(dayOfBirthError ? View.VISIBLE : View.GONE);
		gendererror.setVisibility(genderError ? View.VISIBLE : View.GONE);
		fileNumberError.setVisibility(patientFileNumberError ? View.VISIBLE : View.GONE);

	}

	private Person createPerson() {
		Person person = new Person();

		// Add address
		PersonAddress address = new PersonAddress();
		address.getPreferred();

		List<PersonAddress> addresses = new ArrayList<>();
		addresses.add(address);
		person.setAddresses(addresses);

		//Add person attributes
		List<PersonAttribute> personAttributeList = new ArrayList<>(personAttributeMap.values());
		person.setAttributes(personAttributeList);

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
		identifier.setIdentifierType(patientIdentifierType);
		identifier.setLocation(loginLocation);

		List<PatientIdentifier> patientIdentifierList = new ArrayList<>();
		patientIdentifierList.add(identifier);
		patient.setIdentifiers(patientIdentifierList);

		patient.setPerson(createPerson());
		patient.setUuid(" ");
		return patient;
	}

	private Patient updatePatient(Patient patient) {
		PatientIdentifier identifier = new PatientIdentifier();
		identifier.setIdentifier(ViewUtils.getInput(fileNumber));
		identifier.setIdentifierType(patientIdentifierType);
		identifier.setLocation(loginLocation);

		List<PatientIdentifier> patientIdentifierList = new ArrayList<>();
		patientIdentifierList.add(identifier);
		patient.setIdentifiers(patientIdentifierList);

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
		intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patient.getPerson().getUuid());
		startActivity(intent);
	}

	@Override
	public void showUpgradeRegistrationModuleInfo() {
		ToastUtil.notifyLong(getResources().getString(R.string.registration_core_info));
	}

	@Override
	public void setPatientIdentifierType(PatientIdentifierType patientIdentifierType) {
		this.patientIdentifierType = patientIdentifierType;
	}

	@Override
	public void showToast(String message, ToastUtil.ToastType toastType) {
		ToastUtil.showShortToast(getContext(), toastType, message);
	}

	@Override
	public void loadPersonAttributeTypes(List<PersonAttributeType> personAttributeTypeList) {
		for (PersonAttributeType personAttributeType : personAttributeTypeList) {
			LinearLayout personLayout = new LinearLayout(getContext());
			personLayout.setOrientation(LinearLayout.VERTICAL);
			TextInputLayout textInputLayout = new TextInputLayout(getContext());
			textInputLayout.setHintTextAppearance(R.style.textInputLayoutHintColor);

			String datatypeClass = personAttributeType.getFormat();
			if (StringUtils.isBlank(datatypeClass)) {
				continue;
			}

			if (datatypeClass.equalsIgnoreCase("java.lang.Boolean")) {
				AppCompatRadioButton booleanType = new AppCompatRadioButton(getContext());
				booleanType.setLayoutParams(marginParams);

				// set default value
				Boolean defaultValue = mPresenter.searchPersonAttributeValueByType(personAttributeType);
				if (defaultValue != null) {
					booleanType.setChecked(defaultValue);
				}

				textInputLayout.addView(booleanType);
				viewPersonAttributeTypeMap.put(booleanType, personAttributeType);
			} else if (datatypeClass.equalsIgnoreCase("org.openmrs.customdatatype.datatype.DateDatatype")) {
				EditText dateType = new EditText(getContext());
				dateType.setFocusable(true);
				dateType.setTextSize(14);
				dateType.setLayoutParams(marginParams);

				// set default value
				String defaultValue = mPresenter.searchPersonAttributeValueByType(personAttributeType);
				if (StringUtils.notEmpty(defaultValue)) {
					dateType.setText(defaultValue);
				}
				textInputLayout.addView(dateType);
				viewPersonAttributeTypeMap.put(dateType, personAttributeType);
			} else if (datatypeClass.equalsIgnoreCase("org.openmrs.Concept")) {
				// get coded concept uuid
				String conceptUuid = personAttributeType.getConcept().getUuid();
				AppCompatSpinner conceptAnswersDropdown = new AppCompatSpinner(getContext());
				conceptAnswersDropdown.setLayoutParams(marginParams);
				mPresenter.getConceptNames(conceptUuid, conceptAnswersDropdown);
				textInputLayout.addView(conceptAnswersDropdown);
				viewPersonAttributeTypeMap.put(conceptAnswersDropdown, personAttributeType);
			} else if (datatypeClass.equalsIgnoreCase("java.lang.String")) {
				EditText editText = new EditText(getContext());
				editText.setTextSize(14);
				editText.setFocusable(true);
				editText.setHint(personAttributeType.toString());
				editText.setLayoutParams(marginParams);
				// set default value
				String defaultValue = mPresenter.searchPersonAttributeValueByType(personAttributeType);
				if (StringUtils.notEmpty(defaultValue)) {
					editText.setText(defaultValue);
				}
				textInputLayout.addView(editText);
				viewPersonAttributeTypeMap.put(editText, personAttributeType);
			}

			personLayout.addView(textInputLayout);
			personLinearLayout.addView(personLayout);
		}
	}

	@Override
	public void updateConceptNamesView(Spinner conceptNamesDropdown, List<ConceptName> conceptNames) {
		PersonAttributeType personAttributeType = viewPersonAttributeTypeMap.get(conceptNamesDropdown);
		ArrayAdapter<ConceptName> conceptNameArrayAdapter = new ArrayAdapter<ConceptName>(this.getActivity(),
				android.R.layout.simple_spinner_dropdown_item, conceptNames);
		conceptNamesDropdown.setAdapter(conceptNameArrayAdapter);

		// set existing visit attribute if any
		Object personAttributeValue = mPresenter.searchPersonAttributeValueByType(personAttributeType);
		String conceptAnswer = personAttributeValue instanceof ConceptName ? ((ConceptName)personAttributeValue).getUuid()
				: null;

		System.out.println(conceptAnswer + "Answer");

		if (null != conceptAnswer) {
			setDefaultDropdownSelection(conceptNameArrayAdapter, conceptAnswer, conceptNamesDropdown);
		}

		conceptNamesDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				ConceptName conceptName = conceptNames.get(position);
				PersonAttribute personAttribute = new PersonAttribute();
				personAttribute.setValue(String.valueOf(conceptName.getAnswer_concept()));
				personAttribute.setAttributeType(personAttributeType);
				personAttributeMap.put(personAttributeType.getUuid(), personAttribute);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

	}

	@Override
	public void setLoginLocation(Location location) {
		this.loginLocation = location;
	}

	@Override
	public void fillFields(final Patient patient) {
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

			PatientIdentifier patientIdentifier = patient.getIdentifier();
			fileNumber.setText(patientIdentifier.getIdentifier());

		}
	}

	private void addListeners() {

		gen.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
				gendererror.setVisibility(View.GONE);
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
				if (!mPresenter.isRegisteringPatient()) {
					buildPersonAttributeValues();
				}
				mPresenter.confirmRegister(createPatient());
			}
		});

		TextWatcher textWatcher = new PatientBirthdateValidatorWatcher(eddob, edmonth, edyr);
		edmonth.addTextChangedListener(textWatcher);
		edyr.addTextChangedListener(textWatcher);
	}

	private <T extends BaseOpenmrsObject> void setDefaultDropdownSelection(ArrayAdapter<T> arrayAdapter, String searchUuid,
			Spinner dropdown) {
		for (int count = 0; count < arrayAdapter.getCount(); count++) {

			System.out.println("the array adapter" + arrayAdapter.getItem(count).getUuid() + "==" + count);
			System.out.println("the search uuid" + searchUuid + "==" + count);

			if (arrayAdapter.getItem(count).getUuid().equalsIgnoreCase(searchUuid)) {
				dropdown.setSelection(count);
			}
		}
	}

	private void buildPersonAttributeValues() {
		for (Map.Entry<View, PersonAttributeType> set : viewPersonAttributeTypeMap.entrySet()) {
			View componentType = set.getKey();
			PersonAttribute personAttribute = new PersonAttribute();
			personAttribute.setAttributeType(set.getValue());

			if (componentType instanceof RadioButton) {
				personAttribute.setValue(((RadioButton)componentType).isChecked());
			} else if (componentType instanceof EditText) {
				personAttribute.setValue(ViewUtils.getInput((EditText)componentType));
			}

			if (personAttribute.getValue() != null) {
				personAttributeMap.put(set.getValue().getUuid(), personAttribute);

			}

		}
	}

	private void buildMarginLayout() {
		if (marginParams == null) {
			marginParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			marginParams.setMargins(30, 10, 30, 20);
		}
	}
}
