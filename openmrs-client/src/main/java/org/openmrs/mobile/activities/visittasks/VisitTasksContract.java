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

package org.openmrs.mobile.activities.visittasks;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.VisitPredefinedTask;
import org.openmrs.mobile.models.VisitTask;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public interface VisitTasksContract {

	interface View extends BaseView<Presenter> {
		void showToast(String message, ToastUtil.ToastType toastType);

		void getVisitTasks(List<VisitTask> visitTaskList);

		void showAddTaskDialog(Boolean visibility);

		void setPredefinedTasks(List<VisitPredefinedTask> predefinedTasks);
	}

	interface Presenter extends BasePresenterContract {

		void getPredefinedTasks();

		void getVisitTasks();

		void displayAddTask(Boolean visibility);

		void addVisitTasks(String visitTasks);
	}

}
