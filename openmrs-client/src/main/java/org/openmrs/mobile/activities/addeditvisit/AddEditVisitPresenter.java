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

import android.widget.Spinner;

import org.greenrobot.greendao.annotation.NotNull;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.impl.ConceptNameDataService;
import org.openmrs.mobile.data.impl.LocationDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.ProviderDataService;
import org.openmrs.mobile.data.impl.VisitAttributeTypeDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.data.impl.VisitTypeDataService;
import org.openmrs.mobile.models.ConceptName;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttribute;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddEditVisitPresenter extends BasePresenter implements AddEditVisitContract.Presenter {

	@NotNull
	private AddEditVisitContract.View addEditVisitView;

	private Patient patient;
	private Visit visit;
	private VisitAttributeTypeDataService visitAttributeTypeDataService;
	private VisitTypeDataService visitTypeDataService;
	private ConceptNameDataService conceptNameDataService;
	private VisitDataService visitDataService;
	private PatientDataService patientDataService;
	private ProviderDataService providerDataService;
	private LocationDataService locationDataService;
	private boolean processing;
	private String patientUuid;
	private Provider provider;
	private Location location;

	public AddEditVisitPresenter(@NotNull AddEditVisitContract.View addEditVisitView, String patientUuid) {
		this(addEditVisitView, patientUuid, null, null, null, null, null, null, null);
	}

	public AddEditVisitPresenter(@NotNull AddEditVisitContract.View addEditVisitView, String patientUuid,
			VisitDataService visitDataService, PatientDataService patientDataService,
			VisitTypeDataService visitTypeDataService, VisitAttributeTypeDataService visitAttributeTypeDataService,
			ConceptNameDataService conceptNameDataService, ProviderDataService providerDataService,
			LocationDataService locationDataService) {
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

		if (conceptNameDataService == null) {
			this.conceptNameDataService = new ConceptNameDataService();
		} else {
			this.conceptNameDataService = conceptNameDataService;
		}

		if (providerDataService == null) {
			this.providerDataService = new ProviderDataService();
		} else {
			this.providerDataService = providerDataService;
		}

		if (locationDataService == null) {
			this.locationDataService = new LocationDataService();
		} else {
			this.locationDataService = locationDataService;
		}

		this.visit = new Visit();

	}

	/**
	 * TODO: create a service to getProviderByPerson, move code to commons
	 */
	private void getCurrentProvider() {
		String personUuid = OpenMRS.getInstance().getCurrentLoggedInUserInfo().get(ApplicationConstants.UserKeys.USER_UUID);
		if (StringUtils.notEmpty(personUuid)) {

			providerDataService.getAll(false, null, new DataService.GetMultipleCallback<Provider>() {
				@Override
				public void onCompleted(List<Provider> entities, int length) {
					for (Provider entity : entities) {
						if (null != entity.getPerson() && personUuid.equalsIgnoreCase(entity.getPerson().getUuid())) {

							provider = entity;
						}
					}
				}

				@Override
				public void onError(Throwable t) {
					ToastUtil.error(t.getMessage());
				}
			});
		}
	}

	@Override
	public void subscribe() {
		loadPatient();
		getCurrentProvider();
		getLocation();
	}

	private void loadPatient() {
		if (StringUtils.notEmpty(patientUuid)) {
			patientDataService.getByUUID(patientUuid, new DataService.GetSingleCallback<Patient>() {
				@Override
				public void onCompleted(Patient entity) {
					setPatient(entity);
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
		visitDataService.getByPatient(patient, false, null, new DataService.GetMultipleCallback<Visit>() {
			@Override
			public void onCompleted(List<Visit> entities, int length) {
				if (entities.size() > 0) {
					visit = entities.get(0);
					addEditVisitView.initView(false);
				} else {
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
				.getAll(false, new PagingInfo(), new DataService.GetMultipleCallback<VisitAttributeType>() {
					@Override
					public void onCompleted(List<VisitAttributeType> entities, int length) {
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
		visitTypeDataService.getAll(false, null, new DataService.GetMultipleCallback<VisitType>() {
			@Override
			public void onCompleted(List<VisitType> entities, int length) {
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
		locationDataService.getByUUID(OpenMRS.getInstance().getLocation(), new DataService.GetSingleCallback<Location>() {
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
	public void getConceptNames(String uuid, Spinner dropdown) {
		conceptNameDataService.getByConceptUuid(uuid, new DataService.GetMultipleCallback<ConceptName>() {
			@Override
			public void onCompleted(List<ConceptName> entities, int length) {
				addEditVisitView.updateConceptNamesView(dropdown, entities);
			}

			@Override
			public void onError(Throwable t) {
				ToastUtil.error(t.getMessage());
			}
		});
	}

	@Override
	public Patient getPatient() {
		return patient;
	}

	@Override
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@Override
	public Visit getVisit() {
		return visit;
	}

	@Override
	public void startVisit(List<VisitAttribute> attributes) {
		visit.setAttributes(attributes);
		visit.setPatient(patient);
		if (null != location) {
			visit.setLocation(location.getParentLocation());
		}

		setProcessing(true);

		visitDataService.create(visit, new DataService.GetSingleCallback<Visit>() {
			@Override
			public void onCompleted(Visit entity) {
				setProcessing(false);
				addEditVisitView.setSpinnerVisibility(false);
				addEditVisitView.showPatientDashboard();
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
		List<VisitAttribute> existingAttributes = visit.getAttributes();
		//void existing attributes
		for (VisitAttribute visitAttribute : existingAttributes) {
			visitAttribute.setUuid(null);
			if (attributes.contains(visitAttribute)) {
				visitAttribute.setVoided(true);
				visitAttribute.setDateVoided(new Date());
			}
		}

		//visit.setPatient(null);
		setProcessing(true);
		visitDataService.update(visit, new DataService.GetSingleCallback<Visit>() {
			@Override
			public void onCompleted(Visit entity) {
				setProcessing(false);
				addEditVisitView.setSpinnerVisibility(false);
				addEditVisitView.showPatientDashboard();
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
	public void endVisit(String uuid) {
		visitDataService.endVisit(uuid,
				DateUtils.convertTime(System.currentTimeMillis(), DateUtils.OPEN_MRS_REQUEST_FORMAT),
				new DataService.GetSingleCallback<Visit>() {
					@Override
					public void onCompleted(Visit entity) {

					}

					@Override
					public void onError(Throwable t) {
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
					return (T)visitAttribute.getValue();
				}
			}
		}
		return null;
	}

	@Override
	public Provider getProvider() {
		return provider;
	}
}
