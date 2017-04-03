package org.openmrs.mobile.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;


import org.openmrs.mobile.R;

import java.util.Calendar;


public class RegisterPatient extends Fragment {
    private TextView tvDisplayDate;
    private DatePicker dpResult;
    private Button btnChangeDate;
    private int year;
    private int month;
    private int day;

    static final int DATE_DIALOG_ID = 999;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.nav_register_patient);
        View view = inflater.inflate(R.layout.fragment_register_patient, container, false);

       /* tvDisplayDate = (TextView) view.findViewById(R.id.tvDate);
        dpResult = (DatePicker) view.findViewById(R.id.dpResult);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // set current date into textview
        tvDisplayDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

        // set current date into datepicker
        dpResult.init(year, month, day, null);*/

        return view;
    }
}