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
package org.openmrs.mobile.activities.addeditvisit;

import android.widget.Spinner;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.ConceptName;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttribute;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.models.VisitType;

import java.util.List;

public interface AddEditVisitContract {

	interface View extends BaseView<Presenter> {

		void setVisitTitleText(String text);

		void initView(boolean startVisit);

		void setSpinnerVisibility(boolean visibility);

		void loadVisitAttributeTypeFields(List<VisitAttributeType> visitAttributeTypes);

		void updateVisitTypes(List<VisitType> visitTypes);

		void updateConceptNamesView(Spinner conceptNamesDropdown, List<ConceptName> conceptNames);

		void showPatientDashboard();
	}

	interface Presenter extends BasePresenterContract {

		List<VisitAttributeType> loadVisitAttributeTypes();

		Patient getPatient();

		void setPatient(Patient patient);

		Visit getVisit();

		void startVisit(List<VisitAttribute> attributes);

		void updateVisit(List<VisitAttribute> attributes);

		<T> T searchVisitAttributeValueByType(VisitAttributeType visitAttributeType);

		void getConceptNames(String uuid, Spinner conceptAnswersDropdown);

		boolean isProcessing();

		void setProcessing(boolean processing);

		Provider getProvider();

		void endVisit(String uuid);
	}
}

