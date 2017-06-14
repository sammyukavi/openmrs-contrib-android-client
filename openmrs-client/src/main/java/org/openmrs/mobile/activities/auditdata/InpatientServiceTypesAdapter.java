package org.openmrs.mobile.activities.auditdata;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.openmrs.mobile.R;

import java.util.ArrayList;

public class InpatientServiceTypesAdapter extends BaseAdapter {

	private final ArrayList<InpatientServiceType> inpatientServiceTypes;
	private final Context context;

	public InpatientServiceTypesAdapter(Context context,
			int container_spinner_row, ArrayList<InpatientServiceType> inpatientServiceTypes) {
		this.inpatientServiceTypes = inpatientServiceTypes;
		this.context = context;
	}

	@Override
	public int getCount() {
		return inpatientServiceTypes.size();
	}

	@Override
	public Object getItem(int position) {
		return inpatientServiceTypes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		InpatientServiceType inpatientServiceType = inpatientServiceTypes.get(position);

		LayoutInflater inflater = ((AuditDataActivity)context).getLayoutInflater();
		View row = inflater.inflate(R.layout.container_spinner_row, null);

		TextView obsName = (TextView)row.findViewById(R.id.obsName);

		obsName.setText(inpatientServiceType.getName());

		return row;
	}

}
