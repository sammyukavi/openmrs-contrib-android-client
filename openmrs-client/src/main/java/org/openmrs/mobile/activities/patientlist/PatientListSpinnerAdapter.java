package org.openmrs.mobile.activities.patientlist;

import java.util.List;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import org.openmrs.mobile.R;
import org.openmrs.mobile.models.PatientList;

public class PatientListSpinnerAdapter extends ArrayAdapter<PatientList> {

	LayoutInflater layoutInflater;

	private List<PatientList> patientLists;
	private List<PatientList> selectedPatientLists;

	public PatientListSpinnerAdapter(@NonNull Context context, List<PatientList> patientLists,
			List<PatientList> selectedPatientLists) {
		super(context, R.layout.image_spinner_dropdown_item, patientLists);

		this.patientLists = patientLists;
		this.selectedPatientLists = selectedPatientLists;

		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return handleViewCreation(position, convertView, parent);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return handleViewCreation(position, convertView, parent);
	}

	private View handleViewCreation(int position, View convertView, ViewGroup parent) {
		View itemView = layoutInflater.inflate(R.layout.image_spinner_dropdown_item, parent, false);
		PatientListSyncViewHolder patientListSyncViewHolder = new PatientListSyncViewHolder(itemView);

		PatientList patientList = getItem(position);
		patientListSyncViewHolder.patientListText.setText(patientList.toString());
		if (selectedPatientLists.contains(patientList)) {
			patientListSyncViewHolder.syncIcon.setVisibility(View.VISIBLE);
		} else {
			patientListSyncViewHolder.syncIcon.setVisibility(View.INVISIBLE);
		}

		return itemView;
	}

	private class PatientListSyncViewHolder {
		CheckedTextView patientListText;
		ImageView syncIcon;

		public PatientListSyncViewHolder(View view) {
			patientListText = (CheckedTextView) view.findViewById(R.id.checkedTextView);
			syncIcon = (ImageView) view.findViewById(R.id.imageView);
		}
	}
}
