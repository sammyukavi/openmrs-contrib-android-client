package org.openmrs.mobile.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openmrs.mobile.R;
import org.openmrs.mobile.adapters.TabsPageView;


public class VisitTasks extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.nav_visit_tasks);

        View view = inflater.inflate(R.layout.fragment_visit_tasks, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        return view;
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_tab_diagnoses);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_tab_vitals);
        //tabLayout.getTabAt(2).setIcon(R.drawable.ic_tab_bills);
        //tabLayout.getTabAt(3).setIcon(R.drawable.ic_tab_appointments);
        //tabLayout.getTabAt(4).setIcon(R.drawable.ic_tab_allergies);
        //tabLayout.getTabAt(5).setIcon(R.drawable.ic_tab_recent_visits);
    }

    private void setupViewPager(android.support.v4.view.ViewPager viewPager) {
        TabsPageView adapter = new TabsPageView(getActivity().getSupportFragmentManager());

        Bundle bundle = new Bundle();
        //bundle.putSerializable(ApplicationConstants.Tags.PATIENT_DATA, patient);

        Fragment tasksToDo = new TasksTodo();
        tasksToDo.setArguments(bundle);
        adapter.addFragment( tasksToDo, getResources().getString(R.string.label_tasks));

        Fragment tasksComplete = new TasksComplete();
        tasksComplete.setArguments(bundle);
        adapter.addFragment(tasksComplete, getResources().getString(R.string.label_complete));

        viewPager.setAdapter(adapter);
    }
}