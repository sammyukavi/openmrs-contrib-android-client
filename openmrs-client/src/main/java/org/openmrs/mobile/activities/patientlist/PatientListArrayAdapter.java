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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.openmrs.mobile.models.PatientList;

import java.util.List;

/**
 * Handle {@link PatientList} dropdown menu
 */
public class PatientListArrayAdapter extends ArrayAdapter<PatientList> {

    public PatientListArrayAdapter(Context context, List<PatientList> patientLists) {
        super(context, android.R.layout.simple_spinner_item, patientLists);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view;
        if (position == 0) {
            TextView textView = new TextView(getContext());
            textView.setHeight(0);
            textView.setVisibility(View.GONE);
            view = textView;
        }
        else {
            view = super.getDropDownView(position, null, parent);
        }
        parent.setVerticalScrollBarEnabled(false);
        return view;
    }
}
