package org.openmrs.mobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.R;
import org.openmrs.mobile.adapters.ListPatients;
import org.openmrs.mobile.sampledata.Patient;


import java.util.ArrayList;
import java.util.List;


public class ActiveVisits extends Fragment {
    private List<Patient> patientList;
    private RecyclerView patientsView;
    private Patient patient = new Patient();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.nav_active_visits);
        View view = inflater.inflate(R.layout.fragment_active_visits, container, false);
        patientsView = (RecyclerView) view.findViewById(R.id.active_visits_list);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        patientsView.setLayoutManager(llm);
        patientsView.setHasFixedSize(true);
        try {
            initializeData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initializeAdapter();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void initializeData() throws JSONException {
        patientList = new ArrayList<>();
        JSONArray patients = patient.getPatients();
        for (int i = 0; i < patients.length(); i++) {
            JSONObject patientData = new JSONObject(patients.get(i).toString());
            if (Integer.parseInt(patientData.get("active_visit").toString()) == 1) {
                patientList.add(patient.newPatient(patientData.get("visit_date").toString(),
                        patientData.get("end_date").toString(),
                        patientData.get("visit_tag").toString(), patientData.get("id").toString(),
                        Integer.parseInt(patientData.get("age").toString()),
                        patientData.get("gender").toString().charAt(0), Integer.parseInt(patientData.get("active_visit").toString())));
            }
        }
    }

    private void initializeAdapter() {
        ListPatients adapter = new ListPatients(getActivity(), patientList);
        patientsView.setAdapter(adapter);
    }


}
