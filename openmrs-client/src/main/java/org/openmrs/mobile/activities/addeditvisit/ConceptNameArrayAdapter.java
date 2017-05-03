package org.openmrs.mobile.activities.addeditvisit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.ConceptName;

import java.util.List;

public class ConceptNameArrayAdapter extends ArrayAdapter<ConceptName> {

    public ConceptNameArrayAdapter(@NonNull Context context, List<ConceptName> conceptNames) {
        super(context, android.R.layout.simple_spinner_item, conceptNames);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ConceptName conceptName = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item_concept_name, parent, false);
        }

        TextView conceptNameText = (TextView) convertView.findViewById(R.id.conceptName);
        conceptNameText.setText(conceptName.getName());

        return convertView;
    }
}
