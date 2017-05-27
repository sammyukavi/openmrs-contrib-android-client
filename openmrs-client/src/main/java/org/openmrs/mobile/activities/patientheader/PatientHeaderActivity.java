package org.openmrs.mobile.activities.patientheader;

import android.os.Bundle;
import android.view.Menu;

import org.openmrs.mobile.activities.ACBaseActivity;

public class PatientHeaderActivity extends ACBaseActivity{

	public PatientHeaderContract.Presenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}
}
