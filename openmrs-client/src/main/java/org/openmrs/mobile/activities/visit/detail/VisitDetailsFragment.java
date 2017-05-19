/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities.visit.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.visit.VisitContract;
import org.openmrs.mobile.activities.visit.VisitFragment;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

public class VisitDetailsFragment extends VisitFragment implements VisitContract.VisitDetailsView {

	private TextView visitDate, bedNumber, ward, visitType;
	private ImageView activeStatus;
	private Visit visit;

	public static VisitDetailsFragment newInstance() {
		return new VisitDetailsFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setPresenter(mPresenter);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_visit_details, container, false);
		resolveViews(root);
		((VisitDetailsPresenter)mPresenter).getVisit();
		return root;
	}

	private void resolveViews(View v) {
		activeStatus = (ImageView)v.findViewById(R.id.activeStatus);
		visitDate = (TextView)v.findViewById(R.id.visitDates);
		bedNumber = (TextView)v.findViewById(R.id.fetchedBedNumber);
		ward = (TextView)v.findViewById(R.id.fetchedWard);
		visitType = (TextView)v.findViewById(R.id.visitType);
	}

	@Override
	public void showToast(String message, ToastUtil.ToastType toastType) {

	}

	@Override
	public void setVisit(Visit visit) {
		this.visit = visit;
		if (visit != null) {
			if (StringUtils.notNull(visit.getStopDatetime())) {
				activeStatus.setVisibility(View.GONE);
				visitDate.setText(getContext().getString(R.string.visit_label) + ": " + DateUtils
						.convertTime1(visit.getStartDatetime(), DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT) + " - "
						+ DateUtils.convertTime1(visit.getStopDatetime(), DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT));
			} else {
				activeStatus.setVisibility(View.VISIBLE);
				visitDate.setText(getContext().getString(R.string.active_visit_label) + ": " + DateUtils.convertTime1
						(visit.getStartDatetime(), DateUtils.DATE_FORMAT) + " ( since " + DateUtils.convertTime1
						(visit.getStartDatetime(), DateUtils.TIME_FORMAT) + " )");
			}

			if (visit.getVisitType() != null) {
				visitType.setText(visit.getVisitType().getDisplay());
			} else {
				visitType.setText(ApplicationConstants.EMPTY_STRING);
			}

			if (visit.getAttributes().size() != 0) {
				for (int i = 0; i < visit.getAttributes().size(); i++) {
					if (visit.getAttributes().get(i).getUuid().equalsIgnoreCase(ApplicationConstants.visitAttributeTypes
							.BED_NUMBER_UUID)) {
						bedNumber.setText(visit.getAttributes().get(i).getValue().toString());
					} else if (visit.getAttributes().get(i).getUuid()
							.equalsIgnoreCase(ApplicationConstants.visitAttributeTypes.WARD_UUID)) {
						ward.setText(visit.getAttributes().get(i).getValue().toString());


					}
				}
			}
		}

	}
}
