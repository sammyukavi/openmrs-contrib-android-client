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
package org.openmrs.mobile.activities.addeditvisit;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.VisitType;

import java.util.List;

public class VisitTypeArrayAdapter extends ArrayAdapter<VisitType> {

    public VisitTypeArrayAdapter(@NonNull Context context, List<VisitType> visitTypes) {
        super(context, android.R.layout.simple_spinner_item, visitTypes);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        VisitType visitType = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item_visit_type, parent, false);
        }

        TextView visitTypeName = (TextView) convertView.findViewById(R.id.visitTypeName);
        visitTypeName.setText(visitType.getName());

        return convertView;
    }
}
