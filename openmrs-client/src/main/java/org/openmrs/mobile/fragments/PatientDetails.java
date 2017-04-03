package org.openmrs.mobile.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.adapters.TabsPageView;
import org.openmrs.mobile.sampledata.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;


public class PatientDetails extends Fragment {

    private FloatingActionMenu floatingActionMenu;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Patient patient;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_patient_details);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        patient = (Patient) getArguments().getSerializable(ApplicationConstants.Tags.PATIENT_DATA);
        View view = layoutInflater.inflate(R.layout.fragment_patient_details, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.floatingActionMenu);
        floatingActionMenu.setClosedOnTouchOutside(true);

        FloatingActionButton createOldVisit = (FloatingActionButton) view.findViewById(R.id.create_past_visit);
        FloatingActionButton startNewVisit = (FloatingActionButton) view.findViewById(R.id.start_visit);
        FloatingActionButton visitTasks = (FloatingActionButton) view.findViewById(R.id.visit_tasks);
        FloatingActionButton endVisit = (FloatingActionButton) view.findViewById(R.id.end_visit);
        FloatingActionButton visitNote = (FloatingActionButton) view.findViewById(R.id.visit_note);
        FloatingActionButton admitInpatient = (FloatingActionButton) view.findViewById(R.id.admit_inpatient);
        FloatingActionButton captureVitals = (FloatingActionButton) view.findViewById(R.id.capture_vitals);
        FloatingActionButton createPastVisit = (FloatingActionButton) view.findViewById(R.id.create_past_visit);

        TextView given_name = (TextView) view.findViewById(R.id.given_name);
        TextView middle_name = (TextView) view.findViewById(R.id.middle_name);
        TextView family_name = (TextView) view.findViewById(R.id.family_name);
        TextView age = (TextView) view.findViewById(R.id.age);
        TextView id = (TextView) view.findViewById(R.id.patient_id);
        ImageView gender = (ImageView) view.findViewById(R.id.genderIcon);
        ImageView active_visit = (ImageView) view.findViewById(R.id.activeVisitIcon);

        given_name.setText(patient.given_name);
        middle_name.setText(patient.middle_name);
        family_name.setText(patient.family_name);
        age.setText(String.valueOf(patient.age));
        id.setText(patient.id);
        gender.setImageResource(String.valueOf(patient.gender).toLowerCase().equals("m") ? R.drawable.ic_male : R.drawable.ic_female);
        if (patient.active_visit == 1) {
            active_visit.setVisibility(View.VISIBLE);
            visitTasks.setVisibility(View.VISIBLE);
            endVisit.setVisibility(View.VISIBLE);
            visitNote.setVisibility(View.VISIBLE);
            admitInpatient.setVisibility(View.VISIBLE);
            captureVitals.setVisibility(View.VISIBLE);
        } else {
            startNewVisit.setVisibility(View.VISIBLE);
        }


        return view;
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_tab_diagnoses);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_tab_vitals);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_tab_bills);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_tab_appointments);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_tab_allergies);
        tabLayout.getTabAt(5).setIcon(R.drawable.ic_tab_recent_visits);
    }

    private void setupViewPager(ViewPager viewPager) {
        TabsPageView adapter = new TabsPageView(getActivity().getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putSerializable(ApplicationConstants.Tags.PATIENT_DATA, patient);

        Fragment diagnoses = new Diagnoses();
        diagnoses.setArguments(bundle);
        adapter.addFragment(diagnoses, getResources().getString(R.string.label_diagnoses));

        Fragment vitals = new Vitals();
        vitals.setArguments(bundle);
        adapter.addFragment(vitals, getResources().getString(R.string.label_vitals));

        //Fragment bills = new Bills();
        //bills.setArguments(bundle);
        //adapter.addFragment(bills, getResources().getString(R.string.label_bills));

        Fragment appointments = new Appointments();
        appointments.setArguments(bundle);
        adapter.addFragment(appointments, getResources().getString(R.string.label_appointments));

        Fragment allergies = new Allergies();
        allergies.setArguments(bundle);
        adapter.addFragment(allergies, getResources().getString(R.string.label_allergies));

        Fragment recentVisits = new RecentVisits();
        recentVisits.setArguments(bundle);
        adapter.addFragment(recentVisits, getResources().getString(R.string.label_recent_visits));

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
    }

}