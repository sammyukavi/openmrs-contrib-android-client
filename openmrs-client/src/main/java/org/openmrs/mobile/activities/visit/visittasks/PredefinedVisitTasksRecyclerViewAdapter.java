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

package org.openmrs.mobile.activities.visit.visittasks;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.VisitPredefinedTask;

import java.util.List;

class PredefinedVisitTasksRecyclerViewAdapter
		extends RecyclerView.Adapter<PredefinedVisitTasksRecyclerViewAdapter.FetchedVisitTasksHolder> {

	private Activity mContext;
	private List<VisitPredefinedTask> visitPredefinedTasks;
	private CompoundButton.OnCheckedChangeListener view;

	PredefinedVisitTasksRecyclerViewAdapter(Activity context, List<VisitPredefinedTask> visitPredefinedTasks,
			CompoundButton.OnCheckedChangeListener view) {
		this.mContext = context;
		this.visitPredefinedTasks = visitPredefinedTasks;
		this.view = view;
	}

	@Override
	public FetchedVisitTasksHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.predefined_listview, parent, false);
		return new FetchedVisitTasksHolder(itemView);
	}

	@Override
	public void onBindViewHolder(FetchedVisitTasksHolder holder, int position) {
		final VisitPredefinedTask visitPredefinedTaskList = visitPredefinedTasks.get(position);
		try {
			holder.visitTasksLabel.setText(visitPredefinedTaskList.getName());

		} catch (Exception e) {
			holder.visitTasksLabel.setText("");
		}
	}

	@Override
	public int getItemCount() {
		return visitPredefinedTasks == null ? 0 : visitPredefinedTasks.size();
	}

	class FetchedVisitTasksHolder extends RecyclerView.ViewHolder {
		private LinearLayout linearLayout;
		private TextView visitTasksLabel, visitTasksNumber;

		FetchedVisitTasksHolder(View itemView) {
			super(itemView);
			linearLayout = (LinearLayout)itemView;
			visitTasksLabel = (TextView)itemView.findViewById(R.id.predefinedTaskName);
		}
	}
}