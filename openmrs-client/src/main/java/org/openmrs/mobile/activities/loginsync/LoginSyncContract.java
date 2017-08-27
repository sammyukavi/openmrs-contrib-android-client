package org.openmrs.mobile.activities.loginsync;

import android.app.Activity;
import android.support.annotation.Nullable;
import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.event.SyncEvent;
import org.openmrs.mobile.event.SyncPullEvent;
import org.openmrs.mobile.event.SyncPushEvent;

public interface LoginSyncContract {

	interface View extends BaseView<Presenter> {

		void updateSyncPushProgress(double percentComplete, String progressText, @Nullable String durationText);

		void updateSyncPullProgress(double percentComplete, String progressText, @Nullable String durationText);

		void updateSyncPushDuration(String durationText);

		void updateSyncPullDuration(String durationText);

		void finish();

		Activity getParentActivity();

		void notify(String notification);
	}

	interface Presenter extends BasePresenterContract {

		void onSyncPushEvent(SyncPushEvent syncPushEvent);

		void onSyncPullEvent(SyncPullEvent syncPullEvent);

		void onSyncEvent(SyncEvent syncEvent);

		void startMeasuringConnectivity();

		void stopMeasuringConnectivity();

		void sync();
	}
}
