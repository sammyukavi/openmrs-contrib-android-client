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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientListContextModel;

import java.util.List;

/**
 * Display {@link PatientListContextModel}s
 */
public class PatientListModelRecyclerViewAdapter extends RecyclerView.Adapter<PatientListModelRecyclerViewAdapter.PatientListModelViewHolder>{

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
        final Patient patient = items.get(position).getPatient();

        if (patient.getPerson().getDisplay() != null) {
            holder.displayName.setText(patient.getPerson().getDisplay());
        }

        String genderAge = "Gender: ";
        if (patient.getPerson().getGender() != null) {
            if(patient.getPerson().getGender().equalsIgnoreCase("F")){
                genderAge += "Female";
            } else {
                genderAge += "Male";
            }

            if(patient.getPerson().getAge() != null){
                genderAge += " - Age: " + patient.getPerson().getAge();
            }

            holder.genderAge.setText("(" + genderAge + ")");
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class PatientListModelViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener{
        private LinearLayout rowLayout;
        private TextView displayName;
        private TextView genderAge;

        public PatientListModelViewHolder(View itemView) {
            super(itemView);
            rowLayout = (LinearLayout) itemView;
            displayName = (TextView) itemView.findViewById(R.id.patientListModelDisplayName);
            genderAge = (TextView) itemView.findViewById(R.id.patientListModelGenderAge);
            rowLayout.setOnLongClickListener(this);
            rowLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

}
