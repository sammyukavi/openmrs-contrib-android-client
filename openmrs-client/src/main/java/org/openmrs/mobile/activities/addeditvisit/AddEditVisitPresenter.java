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
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.impl.ConceptDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.VisitAttributeTypeDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.data.impl.VisitTypeDataService;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttribute;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AddEditVisitPresenter extends BasePresenter implements AddEditVisitContract.Presenter {

    @NotNull
    private AddEditVisitContract.View addEditVisitView;

    private Patient patient;
    private Visit visit;
    private VisitAttributeTypeDataService visitAttributeTypeDataService;
    private VisitTypeDataService visitTypeDataService;
    private ConceptDataService conceptDataService;
    private VisitDataService visitDataService;
    private PatientDataService patientDataService;
    private boolean processing;
    private String patientUuid;

    public AddEditVisitPresenter(@NotNull AddEditVisitContract.View addEditVisitView, String patientUuid) {
        this.addEditVisitView = addEditVisitView;
        this.addEditVisitView.setPresenter(this);
        this.patientUuid = patientUuid;
        this.visitAttributeTypeDataService = new VisitAttributeTypeDataService();
        this.visitTypeDataService = new VisitTypeDataService();
        this.patientDataService = new PatientDataService();
        this.conceptDataService = new ConceptDataService();
        this.visitDataService = new VisitDataService();
        this.visit = new Visit();
    }

    @Override
    public void subscribe() {
        loadPatient();
        loadVisitTypes();
        getVisitAttributeTypes();
    }

    private void loadPatient(){
        System.out.println("*******************************************");
        System.out.println("PATIEND UUUID " + patientUuid);
        if(StringUtils.notEmpty(patientUuid)){
            patientDataService.getByUUID(patientUuid, new DataService.GetSingleCallback<Patient>() {
                @Override
                public void onCompleted(Patient entity) {
                    setPatient(entity);
                    loadVisit(entity);
                }

                @Override
                public void onError(Throwable t) {}
            });
        }
    }

    private void loadVisit(Patient patient){
        System.out.println("***************************LOAD VISIT********************************");
        System.out.println("PATIENT " + patient);
        visitDataService.getByPatient(patient, false, null, new DataService.GetMultipleCallback<Visit>() {
            @Override
            public void onCompleted(List<Visit> entities, int length) {
                System.out.println("SUCCESSFUL:VISIT " + "::" + entities.size());
                if( entities.size() > 0){
                    visit = entities.get(0);
                    addEditVisitView.initView(false);
                }

                addEditVisitView.initView(true);

            }

            @Override
            public void onError(Throwable t) {
                System.out.println("ERROR:::VISIT " + t.getMessage());
            }
        });
    }

    @Override
    public List<VisitAttributeType> getVisitAttributeTypes() {
        final List<VisitAttributeType> visitAttributeTypes = new ArrayList<>();
        visitAttributeTypeDataService.getAll(false, new PagingInfo(), new DataService.GetMultipleCallback<VisitAttributeType>() {
            @Override
            public void onCompleted(List<VisitAttributeType> entities, int length) {
                visitAttributeTypes.addAll(entities);
                addEditVisitView.loadVisitAttributeTypeFields(visitAttributeTypes);
                setProcessing(false);
            }

            @Override
            public void onError(Throwable t) {
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
            }
        });
    }

    @Override
    public void getConceptAnswers(String uuid, Spinner dropdown) {
        conceptDataService.getByUUID(uuid, new DataService.GetSingleCallback<Concept>() {
            @Override
            public void onCompleted(Concept entity) {
                addEditVisitView.updateConceptAnswersView(dropdown, entity.getAnswers());
            }

            @Override
            public void onError(Throwable t) {
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
        System.out.println("*******************************************");
        System.out.println("patient = " + patient);
        System.out.println(attributes);
        //visit.setAttributes(attributes);
        visit.setPatient(patient);
        visit.setStartDatetime(
                DateUtils.convertTime(System.currentTimeMillis(), DateUtils.OPEN_MRS_REQUEST_FORMAT));
        //visit.setLocation(new Location());
        setProcessing(true);

        visitDataService.create(visit, new DataService.GetSingleCallback<Visit>() {
            @Override
            public void onCompleted(Visit entity) {
                setProcessing(false);
                addEditVisitView.setSpinnerVisibility(false);
                System.out.println("done saving..." + entity);
                if(null != entity) {
                    visit = entity;
                }
            }

            @Override
            public void onError(Throwable t) {
                setProcessing(false);
                addEditVisitView.setSpinnerVisibility(false);
            }
        });
    }

    @Override
    public void updateVisit() {

    }

    @Override
    public boolean isProcessing() {
        return processing;
    }

    @Override
    public void setProcessing(boolean processing) {
        this.processing = processing;
    }
}
