package org.openmrs.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.openmrs.mobile.R;
import org.openmrs.mobile.fragments.ActiveVisits;
import org.openmrs.mobile.fragments.CaptureVitals;
import org.openmrs.mobile.fragments.FindPatient;
import org.openmrs.mobile.fragments.PatientLists;
import org.openmrs.mobile.fragments.RegisterPatient;
import org.openmrs.mobile.fragments.VisitTasks;
import org.openmrs.mobile.interfaces.PassPatientData;
import org.openmrs.mobile.sampledata.Patient;


public class Dashboard extends AppCompatActivity  {

    Toolbar toolbar;
    FragmentManager fragmentManager;
    DrawerLayout drawerLayout;
    Patient patient;
    String currentSelectedMenuItem;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initializeToolbar();
        initializeNavDrawer();
    }

    /*private void initializeNavDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        fragmentManager = getSupportFragmentManager();
        drawerLayout.setScrimColor(ContextCompat.getColor(this, android.R.color.transparent));
        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.label_open, R.string.label_close);
        mActionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(mActionBarDrawerToggle);
    }*/

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    public void initializeNavDrawer() {

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        fragmentManager = getSupportFragmentManager();
        drawerLayout.setScrimColor(ContextCompat.getColor(this, android.R.color.transparent));
        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.label_open, R.string.label_close);
        mActionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(mActionBarDrawerToggle);

        try {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, FindPatient.class.newInstance()).commit();
            currentSelectedMenuItem = String.valueOf(navigationView.getMenu().getItem(0).getItemId());
        } catch (Exception e) {
            e.printStackTrace();
        }


        navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if (currentSelectedMenuItem.equals(String.valueOf(menuItem.getItemId()))) {
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawers();
                    return true;
                } else {
                    currentSelectedMenuItem = String.valueOf(menuItem.getItemId());
                }

                Class fragmentClass = null;
                Fragment fragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.navItemFindPatient:
                        fragmentClass = FindPatient.class;
                        break;
                    case R.id.navItemActiveVisits:
                        fragmentClass = ActiveVisits.class;
                        break;
                    case R.id.navItemCaptureVitals:
                        fragmentClass = CaptureVitals.class;
                        break;
                    case R.id.navItemregisterPatient:
                        fragmentClass = RegisterPatient.class;
                        break;
                    case R.id.navItemPatientLists:
                        fragmentClass = PatientLists.class;
                        break;
                    case R.id.navItemVisitTasks:
                        fragmentClass = VisitTasks.class;
                        break;
                    case R.id.navItemSettings:
                        Intent settingsIntent = new Intent(getApplicationContext(), Settings.class);
                        startActivity(settingsIntent);
                        return true;
                }

                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                fragmentManager.beginTransaction().setCustomAnimations(
                        R.anim.slide_in_left, R.anim.slide_out_right,
                        R.anim.pop_enter, R.anim.pop_exit).replace(R.id.fragment_container, fragment).addToBackStack(null).commit();


                menuItem.setChecked(true);
                setTitle(menuItem.getTitle());
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    public void initializeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



}

