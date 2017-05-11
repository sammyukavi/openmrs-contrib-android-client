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
package org.openmrs.mobile.listeners.retrofit;

import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PatientListContext;

import java.util.List;

/**
 * Patient list callback listener
 */
public interface PatientListCallbackListener extends DefaultResponseCallbackListener {

	void onGetPatientList(List<PatientList> patientLists);

    void onGetPatientListData(List<PatientListContext> listContextModels);
}
