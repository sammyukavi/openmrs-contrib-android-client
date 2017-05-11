/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.mobile.activities.patientlist;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.models.PatientListContextModel;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.List;

/**
 * Display {@link PatientListContextModel}s
 */
public class PatientListModelRecyclerViewAdapter
		extends RecyclerView.Adapter<PatientListModelRecyclerViewAdapter.PatientListModelViewHolder> {

	private Activity context;
	private PatientListContract.View view;
	private List<PatientListContextModel> items;

	public PatientListModelRecyclerViewAdapter(Activity context,
			List<PatientListContextModel> patientListModels, PatientListContract.View view) {
		this.context = context;
		this.items = patientListModels;
		this.view = view;
	}

	@Override
	public PatientListModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_list_model_row, parent, false);
		return new PatientListModelViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(PatientListModelViewHolder holder, int position) {
		PatientListContextModel patientListContextModel = items.get(position);

		holder.headerContent.setText(StringUtils.stripHtmlTags(patientListContextModel.getHeaderContent()));
		holder.bodyContent.setText(StringUtils.stripHtmlTags(patientListContextModel.getBodyContent()));
		holder.rowLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, PatientDashboardActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE,
						patientListContextModel.getPatient().getUuid());
				context.startActivity(intent);
				context.finish();
			}
		});
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	class PatientListModelViewHolder extends RecyclerView.ViewHolder {
		private LinearLayout rowLayout;
		private TextView headerContent;
		private TextView bodyContent;

		public PatientListModelViewHolder(View itemView) {
			super(itemView);
			rowLayout = (LinearLayout)itemView;
			headerContent = (TextView)itemView.findViewById(R.id.headerContent);
			bodyContent = (TextView)itemView.findViewById(R.id.bodyContent);
		}
	}
}
