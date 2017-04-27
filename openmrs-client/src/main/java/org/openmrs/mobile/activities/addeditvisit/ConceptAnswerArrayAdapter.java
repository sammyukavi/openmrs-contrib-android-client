package org.openmrs.mobile.activities.addeditvisit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.ConceptAnswer;

import java.util.List;

public class ConceptAnswerArrayAdapter extends ArrayAdapter<ConceptAnswer> {

    public ConceptAnswerArrayAdapter(@NonNull Context context, List<ConceptAnswer> conceptAnswers) {
        super(context, android.R.layout.simple_spinner_item, conceptAnswers);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ConceptAnswer conceptAnswer = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item_concept_answer, parent, false);
        }

        TextView conceptAnswerName = (TextView) convertView.findViewById(R.id.conceptAnswerName);
        conceptAnswerName.setText(conceptAnswer.getDisplay());

        return convertView;
    }
}
