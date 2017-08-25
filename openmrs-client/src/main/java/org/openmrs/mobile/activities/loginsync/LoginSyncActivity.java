package org.openmrs.mobile.activities.loginsync;

import android.os.Bundle;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;

public class LoginSyncActivity extends ACBaseActivity {

	public LoginSyncContract.Presenter presenter;

	private EventBus eventBus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_sync);

		// Create fragment
		LoginSyncFragment loginSyncFragment =
				(LoginSyncFragment) getSupportFragmentManager().findFragmentById(R.id.loginSyncContentFrame);
		if (loginSyncFragment == null) {
			loginSyncFragment = LoginSyncFragment.newInstance();
		}
		if (!loginSyncFragment.isActive()) {
			addFragmentToActivity(getSupportFragmentManager(),
					loginSyncFragment, R.id.loginSyncContentFrame);
		}

		presenter = new LoginSyncPresenter(loginSyncFragment, openMRS);
		eventBus = openMRS.getEventBus();
	}

	@Override
	protected void onStart() {
		super.onStart();
		eventBus.register(this);
	}

	@Override
	protected void onStop() {
		eventBus.unregister(this);
		super.onStop();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onSyncPushEvent(int temp) {
		presenter.onSyncPushEvent();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onSyncPullEvent(int temp) {
		presenter.onSyncPullEvent();
	}
}
