package org.openmrs.mobile.activities;


import android.os.Bundle;
import android.preference.PreferenceActivity;

import org.openmrs.mobile.R;


public class Settings extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //We leave this becuase we're supporting API Level 9
        //addPreferencesFromResource(R.xml.settings);

    }
}
