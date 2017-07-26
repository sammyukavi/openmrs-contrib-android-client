package org.openmrs.mobile.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.DiagnosisSearchResult;

import java.util.List;

public class CustomDiagnosesDropdownAdapter extends ArrayAdapter<DiagnosisSearchResult> {
	public CustomDiagnosesDropdownAdapter(Context context, int resource, List<DiagnosisSearchResult> diagnosisSearchResult) {
		super(context, resource, diagnosisSearchResult);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DiagnosisSearchResult diagnosisSearchResult = getItem(position);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View item = inflater.inflate(R.layout.custom_dropdown_item, parent, false);
		TextView label = (TextView)item.findViewById(R.id.textView);
		label.setText(diagnosisSearchResult.toString());
		return item;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(position, convertView, parent);
	}
}