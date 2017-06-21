package org.openmrs.mobile.activities;

import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitNote;

public interface IBaseDiagnosisFragment {

	void setDiagnoses(Visit visit);

	void initializeListeners();

	void initializeViewFields(View v);

	void setSearchDiagnosisView(AutoCompleteTextView view);

	AutoCompleteTextView getSearchDiagnosisView();

	void setNoPrimaryDiagnoses(TextView view);
	TextView getNoPrimaryDiagnoses();

	void setNoSecondaryDiagnoses(TextView view);
	TextView getNoSecondaryDiagnoses();

	void setPrimaryDiagnosesRecycler(RecyclerView view);
	RecyclerView getPrimaryDiagnosesRecycler();

	void setSecondaryDiagnosesRecycler(RecyclerView view);
	RecyclerView getSecondaryDiagnosesRecycler();

	void setSubmitVisitNote(AppCompatButton view);
	AppCompatButton getSubmitVisitNote();

	void setEncounterUuid(String encounterUuid);
	String getEncounterUuid();

	void setClinicalNote(String clinicalNote);
	String getClinicalNote();

	void setVisit(Visit visit);
	Visit getVisit();
}
