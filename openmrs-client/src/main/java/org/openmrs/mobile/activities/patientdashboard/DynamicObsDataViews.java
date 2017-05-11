package org.openmrs.mobile.activities.patientdashboard;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;

public class DynamicObsDataViews {

	private ViewGroup.LayoutParams linearLayoutParams;
	private LinearLayout observationHolder;
	protected ImageView observationIcon;
	protected TextView observationTextView;

	public DynamicObsDataViews(Context context) {
		this.linearLayoutParams =
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams
						.WRAP_CONTENT,
						1.0f);
		observationHolder = new LinearLayout(context);
		observationHolder.setLayoutParams(linearLayoutParams);
		observationHolder.setOrientation(LinearLayout.HORIZONTAL);
		observationHolder.setPadding(0, 0, 0, 0);
		observationIcon = new ImageView(context);
		observationIcon.setImageDrawable(ContextCompat.getDrawable(context, android.R.drawable.ic_menu_edit));
		observationIcon.setPadding(0, 0, 0, 0);
		observationTextView = new TextView(context);
		observationTextView.setPadding(10, 0, 10, 0);
		observationTextView.setGravity(Gravity.LEFT);
		observationTextView.setHintTextColor(ContextCompat.getColor(context, R.color.openmrs_color_grey));
		observationTextView.setTextColor(ContextCompat.getColor(context, R.color.openmrs_color_grey));
	}

	public void setHint(String hint) {
		observationTextView.setHint(hint);
	}

	public void setText(String hint) {
		observationTextView.setText(hint.toString());
	}

	public LinearLayout getLayoutView() {
		observationHolder.addView(observationIcon);
		observationHolder.addView(observationTextView);
		return observationHolder;
	}

	public void setOnClickListener(View.OnClickListener switchToEditMode) {
		observationIcon.setOnClickListener(switchToEditMode);
	}

	public DynamicObsDataViews instance(Context context) {
		return new DynamicObsDataViews(context);
	}
}