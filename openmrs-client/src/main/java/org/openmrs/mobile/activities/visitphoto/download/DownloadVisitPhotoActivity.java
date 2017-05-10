package org.openmrs.mobile.activities.visitphoto.download;

import android.os.Bundle;
import android.view.Menu;

import org.openmrs.mobile.activities.ACBaseActivity;

public class DownloadVisitPhotoActivity extends ACBaseActivity {

	public DownloadVisitPhotoContract.Presenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*getLayoutInflater().inflate(R.layout.activity_addedit_visit, frameLayout);

        Bundle extras = getIntent().getExtras();
        String patientUuid;
        if (extras != null) {
            patientUuid = extras.getString(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
            if(StringUtils.notEmpty(patientUuid)){
                DownloadVisitPhotoFragment visitPhotoFragment =
                        (DownloadVisitPhotoFragment) getSupportFragmentManager().findFragmentById(R.id
                        .photoDownloadsContentFrame);
                if(visitPhotoFragment == null){
                    visitPhotoFragment = DownloadVisitPhotoFragment.newInstance();
                }

                if(!visitPhotoFragment.isActive()){
                    addFragmentToActivity(getSupportFragmentManager(), visitPhotoFragment, R.id.photoDownloadsContentFrame);
                }

                presenter = new DownloadVisitPhotoPresenter(visitPhotoFragment, patientUuid);
            }
        } */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}
}
