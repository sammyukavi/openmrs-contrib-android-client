package org.openmrs.mobile.activities.loginsync;

import android.app.Activity;
import android.support.annotation.Nullable;
import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.event.SyncPullEvent;
import org.openmrs.mobile.event.SyncPushEvent;

public interface LoginSyncContract {

	interface View extends BaseView<Presenter> {

		void updateSyncPushProgress(double percentComplete, String progressText, @Nullable Integer durationTextStringId);

		void updateSyncPullProgress(double percentComplete, String progressText, @Nullable Integer durationTextStringId);

		void updateSyncPushDuration(int durationTextStringId);

		void updateSyncPullDuration(int durationTextStringId);

		void finish();

		Activity getParentActivity();
	}

	interface Presenter extends BasePresenterContract {

		void onSyncPushEvent(SyncPushEvent syncPushEvent);

		void onSyncPullEvent(SyncPullEvent syncPullEvent);

		void startMeasuringConnectivity();

		void stopMeasuringConnectivity();

		void sync();
	}
}
