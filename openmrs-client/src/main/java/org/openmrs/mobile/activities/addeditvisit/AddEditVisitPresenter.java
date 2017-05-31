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
package org.openmrs.mobile.activities.addeditvisit;

import android.support.annotation.NonNull;
import android.widget.Spinner;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.ConceptAnswerDataService;
import org.openmrs.mobile.data.impl.LocationDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.VisitAttributeTypeDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.data.impl.VisitTypeDataService;
import org.openmrs.mobile.models.ConceptAnswer;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttribute;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class AddEditVisitPresenter extends BasePresenter implements AddEditVisitContract.Presenter {

	@NonNull
	private AddEditVisitContract.View addEditVisitView;

	private Visit visit;
	private VisitAttributeTypeDataService visitAttributeTypeDataService;
	private VisitTypeDataService visitTypeDataService;
	private ConceptAnswerDataService conceptAnswerDataService;
	private VisitDataService visitDataService;
	private PatientDataService patientDataService;
	private LocationDataService locationDataService;
	private boolean processing;
	private String patientUuid;
	private Location location;

	public AddEditVisitPresenter(@NonNull AddEditVisitContract.View addEditVisitView, String patientUuid) {
		this(addEditVisitView, patientUuid, null, null, null, null, null, null);
	}

	public AddEditVisitPresenter(@NonNull AddEditVisitContract.View addEditVisitView, String patientUuid,
			VisitDataService visitDataService, PatientDataService patientDataService,
			VisitTypeDataService visitTypeDataService, VisitAttributeTypeDataService visitAttributeTypeDataService,
			ConceptAnswerDataService conceptAnswerDataService, LocationDataService locationDataService) {
		this.addEditVisitView = addEditVisitView;
		this.addEditVisitView.setPresenter(this);
		this.patientUuid = patientUuid;

		if (visitDataService == null) {
			this.visitDataService = new VisitDataService();
		} else {
			this.visitDataService = visitDataService;
		}

		if (visitAttributeTypeDataService == null) {
			this.visitAttributeTypeDataService = new VisitAttributeTypeDataService();
		} else {
			this.visitAttributeTypeDataService = visitAttributeTypeDataService;
		}

		if (visitTypeDataService == null) {
			this.visitTypeDataService = new VisitTypeDataService();
		} else {
			this.visitTypeDataService = visitTypeDataService;
		}

		if (patientDataService == null) {
			this.patientDataService = new PatientDataService();
		} else {
			this.patientDataService = patientDataService;
		}

		if (conceptAnswerDataService == null) {
			this.conceptAnswerDataService = new ConceptAnswerDataService();
		} else {
			this.conceptAnswerDataService = conceptAnswerDataService;
		}

		if (locationDataService == null) {
			this.locationDataService = new LocationDataService();
		} else {
			this.locationDataService = locationDataService;
		}

		this.visit = new Visit();

	}

	@Override
	public void subscribe() {
		loadPatient();
		getLocation();
	}

	private void loadPatient() {
		if (StringUtils.notEmpty(patientUuid)) {
			patientDataService
					.getByUUID(patientUuid, QueryOptions.LOAD_RELATED_OBJECTS, new DataService.GetCallback<Patient>() {
						@Override
						public void onCompleted(Patient entity) {
							loadVisit(entity);
						}

						@Override
						public void onError(Throwable t) {
							ToastUtil.error(t.getMessage());
						}
					});
		}
	}

	private void loadVisit(Patient patient) {
		visitDataService.getByPatient(patient, new QueryOptions(false, true), new PagingInfo(0, 10),
				new DataService.GetCallback<List<Visit>>() {
					@Override
					public void onCompleted(List<Visit> entities) {
						if (entities.size() > 0) {
							visit = entities.get(0);

							addEditVisitView.initView(false);
						} else {
							visit.setPatient(patient);
							addEditVisitView.initView(true);
						}

						loadVisitTypes();

						loadVisitAttributeTypes();
					}

					@Override
					public void onError(Throwable t) {
						ToastUtil.error(t.getMessage());
					}
				});
	}

	@Override
	public List<VisitAttributeType> loadVisitAttributeTypes() {
		final List<VisitAttributeType> visitAttributeTypes = new ArrayList<>();
		visitAttributeTypeDataService
				.getAll(new QueryOptions(false, true), new PagingInfo(0, 100), new DataService
						.GetCallback<List<VisitAttributeType>>() {
					@Override
					public void onCompleted(List<VisitAttributeType> entities) {
						visitAttributeTypes.addAll(entities);
						addEditVisitView.loadVisitAttributeTypeFields(visitAttributeTypes);
						setProcessing(false);
					}

					@Override
					public void onError(Throwable t) {
						ToastUtil.error(t.getMessage());
					}
				});

		return visitAttributeTypes;
	}

	public void loadVisitTypes() {
		visitTypeDataService.getAll(new QueryOptions(false, false), null, new DataService.GetCallback<List<VisitType>>() {
			@Override
			public void onCompleted(List<VisitType> entities) {
				addEditVisitView.updateVisitTypes(entities);
			}

			@Override
			public void onError(Throwable t) {
				ToastUtil.error(t.getMessage());
			}
		});
	}

	/**
	 * TODO: Move to Base class
	 */
	public void getLocation() {
		locationDataService.getByUUID(OpenMRS.getInstance().getLocation(), QueryOptions.LOAD_RELATED_OBJECTS,
				new DataService.GetCallback<Location>() {
					@Override
					public void onCompleted(Location entity) {
						location = entity;
					}

					@Override
					public void onError(Throwable t) {
						ToastUtil.error(t.getMessage());
					}
				});
	}

	@Override
	public void getConceptAnswer(String uuid, Spinner dropdown) {
		conceptAnswerDataService.getByConceptUuid(uuid,null, new DataService.GetCallback<List<ConceptAnswer>>() {
			@Override
			public void onCompleted(List<ConceptAnswer> entities) {
				addEditVisitView.updateConceptAnswersView(dropdown, entities);
			}

			@Override
			public void onError(Throwable t) {
				ToastUtil.error(t.getMessage());
			}
		});
	}

	@Override
	public Patient getPatient() {
		if (null != visit && null != visit.getPatient()) {
			return visit.getPatient();
		}
		return null;
	}

	@Override
	public Visit getVisit() {
		return visit;
	}

	@Override
	public void startVisit(List<VisitAttribute> attributes) {
		visit.setAttributes(attributes);
		if (null != location) {
			visit.setLocation(location.getParentLocation());
		}

		setProcessing(true);

		visitDataService.create(visit, new DataService.GetCallback<Visit>() {
			@Override
			public void onCompleted(Visit entity) {
				setProcessing(false);
				addEditVisitView.setSpinnerVisibility(false);
				addEditVisitView.showVisitDetails(entity.getUuid());
			}

			@Override
			public void onError(Throwable t) {
				setProcessing(false);
				addEditVisitView.setSpinnerVisibility(false);
				ToastUtil.error(t.getMessage());
			}
		});
	}

	@Override
	public void updateVisit(List<VisitAttribute> attributes) {
		Visit updatedVisit = new Visit();
		updatedVisit.setAttributes(attributes);
		updatedVisit.setVisitType(visit.getVisitType());

		setProcessing(true);
		visitDataService.updateVisit(visit.getUuid(), updatedVisit, new DataService.GetCallback<Visit>() {
			@Override
			public void onCompleted(Visit entity) {
				setProcessing(false);
				addEditVisitView.setSpinnerVisibility(false);
				addEditVisitView.showVisitDetails(entity.getUuid());
			}

			@Override
			public void onError(Throwable t) {
				setProcessing(false);
				addEditVisitView.setSpinnerVisibility(false);
				addEditVisitView.showVisitDetails(null);
			}
		});
	}

	@Override
	public void endVisit() {
		if (null == visit || null == visit.getUuid())
			return;

		String uuid = visit.getUuid();
		visit = new Visit();
		visit.setUuid(uuid);
		visit.setStopDatetime(DateUtils.convertTime(System.currentTimeMillis(), DateUtils.OPEN_MRS_REQUEST_FORMAT));
		visitDataService.endVisit(visit.getUuid(), visit, null, new DataService.GetCallback<Visit>() {
			@Override
			public void onCompleted(Visit entity) {
				visit = entity;
				addEditVisitView.showPatientDashboard();
			}

			@Override
			public void onError(Throwable t) {
				System.out.println(t.getLocalizedMessage());
				ToastUtil.error(t.getMessage());
			}
		});
	}

	@Override
	public boolean isProcessing() {
		return processing;
	}

	@Override
	public void setProcessing(boolean processing) {
		this.processing = processing;
	}

	@Override
	public <T> T searchVisitAttributeValueByType(VisitAttributeType visitAttributeType) {
		if (null != getVisit() && null != getVisit().getAttributes()) {
			for (VisitAttribute visitAttribute : getVisit().getAttributes()) {
				if (visitAttribute.getAttributeType().getUuid().equalsIgnoreCase(visitAttributeType.getUuid())) {
					System.out.println((T)visitAttribute.getValue() + " the visit attribute value ");
					return (T)visitAttribute.getValue();
				}
			}
		}
		return null;
	}
}
