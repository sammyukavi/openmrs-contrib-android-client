package org.openmrs.mobile.activities.visitphoto.upload;

import android.os.Bundle;
import android.view.Menu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;

public class UploadVisitPhotoActivity extends ACBaseActivity {

    public UploadVisitPhotoContract.Presenter visitPhotoPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_visit_photo, frameLayout);

        Bundle extras = getIntent().getExtras();
        String patientUuid;
        String visitUuid;
        String providerUuid;
        if (extras != null) {
            patientUuid = extras.getString(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
            visitUuid = extras.getString(ApplicationConstants.BundleKeys.VISIT_ID_BUNDLE);
            providerUuid = extras.getString(ApplicationConstants.BundleKeys.PROVIDER_ID_BUNDLE);
            if(StringUtils.notEmpty(patientUuid) && StringUtils.notEmpty(visitUuid) && StringUtils.notEmpty(providerUuid)){
                UploadVisitPhotoFragment visitPhotoFragment =
                        (UploadVisitPhotoFragment) getSupportFragmentManager().findFragmentById(R.id.visitPhotoContentFrame);
                if(visitPhotoFragment == null){
                    visitPhotoFragment = UploadVisitPhotoFragment.newInstance();
                }

                if(!visitPhotoFragment.isActive()){
                    addFragmentToActivity(getSupportFragmentManager(), visitPhotoFragment, R.id.visitPhotoContentFrame);
                }

                visitPhotoPresenter = new UploadVisitPhotoPresenter(visitPhotoFragment, patientUuid, visitUuid, providerUuid);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }
}
