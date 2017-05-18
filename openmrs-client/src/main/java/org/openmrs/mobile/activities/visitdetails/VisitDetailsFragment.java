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

package org.openmrs.mobile.activities.visitdetails;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.openmrs.mobile.activities.ACBaseFragment;

public class VisitDetailsFragment extends ACBaseFragment<VisitDetailsContract.VisitDetailsMainPresenter>
		implements VisitDetailsContract.ViewVisitDetailsMain {
	private Toolbar toolbar;


	public static VisitDetailsFragment newInstance() {
		return new VisitDetailsFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

}
