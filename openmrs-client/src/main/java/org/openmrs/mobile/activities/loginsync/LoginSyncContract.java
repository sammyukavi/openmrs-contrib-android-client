package org.openmrs.mobile.activities.loginsync;

import android.support.annotation.Nullable;
import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.event.SyncPullEvent;
import org.openmrs.mobile.event.SyncPushEvent;

public interface LoginSyncContract {

	interface View extends BaseView<Presenter> {

		void updateSyncPushProgress(float percentComplete, String progressText, @Nullable String durationText);

		void updateSyncPullProgress(float percentComplete, String progressText, @Nullable String durationText);

		void finish();
	}

	interface Presenter extends BasePresenterContract {

		void onSyncPushEvent(SyncPushEvent syncPushEvent);

		void onSyncPullEvent(SyncPullEvent syncPullEvent);
	}
}
