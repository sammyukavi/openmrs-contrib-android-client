package org.openmrs.mobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.openmrs.mobile.R;
import org.openmrs.mobile.adapters.ListRecentAppointments;
import org.openmrs.mobile.sampledata.Appointment;
import org.openmrs.mobile.sampledata.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.ArrayList;
import java.util.List;


public class Appointments extends Fragment {

    private List<Appointment> appointmentList;
    private RecyclerView appointmentsView;
    private Patient patient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        patient = (Patient) getArguments().getSerializable(ApplicationConstants.Tags.PATIENT_DATA);
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);
        appointmentsView = (RecyclerView) view.findViewById(R.id.appointments_list);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        appointmentsView.setLayoutManager(llm);
        appointmentsView.setHasFixedSize(true);
        initializeData();
        initializeAdapter();
        return view;
    }

    private void initializeData() {
        appointmentList = new ArrayList<>();
        appointmentList.add(new Appointment("08.Feb.2017"));
        appointmentList.add(new Appointment("13.Dec.2016"));
        appointmentList.add(new Appointment("31.Oct.2016"));
        appointmentList.add(new Appointment("26.Oct.2016"));
        appointmentList.add(new Appointment("11.Oct.2016"));
    }

    private void initializeAdapter() {
        ListRecentAppointments listRecentAppointments = new ListRecentAppointments(getActivity(), appointmentList);
        appointmentsView.setAdapter(listRecentAppointments);
    }
}
