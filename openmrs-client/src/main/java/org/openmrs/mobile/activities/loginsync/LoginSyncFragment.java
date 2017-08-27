package org.openmrs.mobile.activities.loginsync;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ToastUtil;

public class LoginSyncFragment extends ACBaseFragment<LoginSyncContract.Presenter> implements LoginSyncContract.View {

	private View rootView;
	private ProgressBar pullProgressBar, pushProgressBar;
	private TextView pullProgressText, pullDurationText, pushProgressText, pushDurationText;

	public static LoginSyncFragment newInstance() {
		return new LoginSyncFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_login_sync, container, false);

		initViewFields();

		// Font config
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		mPresenter.startMeasuringConnectivity();
	}

	@Override
	public void onStop() {
		mPresenter.stopMeasuringConnectivity();
		super.onStop();
	}

	private void initViewFields() {
		pushProgressBar = (ProgressBar) rootView.findViewById(R.id.pushProgressBar);
		pushProgressText = (TextView) rootView.findViewById(R.id.pushProgressText);
		pushDurationText = (TextView) rootView.findViewById(R.id.pushDurationText);

		pullProgressBar = (ProgressBar) rootView.findViewById(R.id.pullProgressBar);
		pullProgressText = (TextView) rootView.findViewById(R.id.pullProgressText);
		pullDurationText = (TextView) rootView.findViewById(R.id.pullDurationText);

		pushProgressText.setText("Checking if data needs to be uploaded...");
		pushDurationText.setText("");
		pullProgressText.setText("Waiting for upload to finish...");
		pullDurationText.setText("");
	}

	public void updateSyncPushProgress(double percentComplete, String progressText, @Nullable String durationText) {
		pushProgressBar.setProgress((int) Math.floor(percentComplete));
		pushProgressText.setText(progressText);

		if (percentComplete == 100) {
			pushDurationText.setVisibility(View.INVISIBLE);
		} else {
			pushDurationText.setText(durationText);
		}
	}

	public void updateSyncPullProgress(double percentComplete, String progressText, @Nullable String durationText) {
		pullProgressBar.setProgress((int) Math.floor(percentComplete));
		pullProgressText.setText(progressText);

		if (percentComplete == 100 || durationText == null) {
			pullDurationText.setVisibility(View.INVISIBLE);
		} else {
			pullDurationText.setText(durationText);
		}
	}

	public void updateSyncPushDuration(String durationText) {
		pushDurationText.setVisibility(View.VISIBLE);
		pushDurationText.setText(durationText);
	}

	public void updateSyncPullDuration(String durationText) {
		pullDurationText.setVisibility(View.VISIBLE);
		pullDurationText.setText(durationText);
	}

	public void finish() {
		getActivity().finish();
	}

	public Activity getParentActivity() {
		return getActivity();
	}

	public void notify(String notification) {
		ToastUtil.notify(notification);
	}
}
