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

		void updateSyncPullProgressForStartingSubscription(double percentComplete, String subscriptionName);

		void updateSyncPullProgressForCompletingSubscription(double percentComplete, String subscriptionName);

		void updateSyncPullProgressForStartingEntity(double percentComplete, String subscriptionName, String entityName);

		void updateSyncPullProgressForCompletingEntity(double percentComplete, String subscriptionName, String entityName);

		void notifySyncPullComplete();

		void notifySyncPushComplete();

		void notifySyncPushConnectionIsSlow();

		void notifySyncPushConnectionIsFast();

		void notifySyncPullConnectionIsSlow();

		void notifySyncPullConnectionIsFast();

		void navigateToNextActivity();

		void notifyConnectionLost();
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
