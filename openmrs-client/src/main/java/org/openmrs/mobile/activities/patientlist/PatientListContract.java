/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.mobile.activities.patientlist;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PatientListContextModel;

import java.util.List;

public interface PatientListContract {

	interface View extends BaseView<Presenter> {

		void setNumberOfPatientsView(int length);

		void setSelectedPatientList(PatientList selectedPatientList);

		void setEmptyPatientListVisibility(boolean visibility);

		void setNoPatientListsVisibility(boolean visibility);

		void updatePatientLists(List<PatientList> patientList);

		void updatePatientListData(List<PatientListContextModel> patientListData);

		void setSpinnerVisibility(boolean visibility);

		boolean isActive();
	}

	interface Presenter extends BasePresenterContract {

		void refresh();

		void getPatientList();

		void getPatientListData(String patientListUuid, int startIndex);

		void setTotalNumberResults(int totalNumberResults);

		int getPage();

		void setPage(int page);

		boolean isLoading();

		void setLoading(boolean loading);

		void loadResults(String patientListUuid, boolean loadNextResults);
	}
}
