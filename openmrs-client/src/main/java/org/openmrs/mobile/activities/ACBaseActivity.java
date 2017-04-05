package org.openmrs.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import org.openmrs.mobile.R;

public class ACBaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    protected FrameLayout frameLayout;
    private static boolean isLaunch = true;
    private DrawerLayout drawer;
    protected static int selectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        intitializeToolbar();

        intitializeNavigationDrawer();

        frameLayout = (FrameLayout) findViewById(R.id.content_frame);

        if (isLaunch) {
            this.isLaunch = false;
            this.selectedId = R.id.navItemFindPatientRecord;
            openActivity(selectedId);
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.isLaunch = false;
        this.selectedId = R.id.navItemFindPatientRecord;
        openActivity(selectedId);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int selectedId = item.getItemId();
        drawer.closeDrawer(GravityCompat.START);
        if (selectedId == this.selectedId) {
            return true;
        }
        openActivity(selectedId);
        return true;
    }


    private void intitializeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    private void intitializeNavigationDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.label_open, R.string.label_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    protected void openActivity(int selectedId) {

        drawer.closeDrawer(GravityCompat.START);
        ACBaseActivity.selectedId = selectedId;

        switch (selectedId) {
            case R.id.navItemFindPatientRecord:
                startActivity(new Intent(this, FindPatientRecord.class));
                break;
            case R.id.navItemActiveVisits:
                startActivity(new Intent(this, ActiveVisits.class));
                break;
            case R.id.navItemCaptureVitals:
                startActivity(new Intent(this, CaptureVitals.class));
                break;
            case R.id.navItemRegisterPatient:
                startActivity(new Intent(this, RegisterPatient.class));
                break;
            case R.id.navItemPatientLists:
                startActivity(new Intent(this, PatientLists.class));
                break;
            case R.id.navItemVisitTasks:
                startActivity(new Intent(this, VisitTasks.class));
                break;
            case R.id.navItemSettings:
                startActivity(new Intent(this, Settings.class));
                break;

            default:
                break;
        }


    }
}
