package org.openmrs.mobile.activities.loginsync;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;

public interface LoginSyncContract {

	interface View extends BaseView<Presenter> {

		void updateSyncPushProgress(int percentComplete, String progressText, String durationText);

		void updateSyncPullProgress(int percentComplete, String progressText, String durationText);

		void finish();
	}

	interface Presenter extends BasePresenterContract {

		void onSyncPushEvent();

		void onSyncPullEvent();
	}
}
