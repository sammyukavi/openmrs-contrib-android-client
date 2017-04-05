package org.openmrs.mobile.activities;

import android.os.Bundle;

import org.openmrs.mobile.R;


public class VisitTasks extends ACBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_find_patient_record, frameLayout);
        setTitle(R.string.nav_visit_tasks);
    }


}
