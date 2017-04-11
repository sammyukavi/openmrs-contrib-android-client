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
package org.openmrs.mobile.test;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.mobile.activities.patientlist.PatientListContract;
import org.openmrs.mobile.activities.patientlist.PatientListPresenter;
import org.openmrs.mobile.api.PatientListRestApi;
import org.openmrs.mobile.listeners.retrofit.PatientListCallbackListener;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PatientListCondition;
import org.openmrs.mobile.models.PatientListContextModel;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.Results;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test {@link PatientList} & ${@link PatientListContextModel} service methods
 */
public class PatientListTest extends ACUnitTestBase{

    @Mock
    private PatientListRestApi restService;

    @Mock
    private PatientListContract.View patientListView;

    private List<PatientList> patientLists;

    private List<PatientListContextModel> patientListData;

    private PatientListPresenter patientListPresenter;

    @Before
    public void before(){
        patientListPresenter = new PatientListPresenter(patientListView, restService);

        patientLists = new ArrayList<>();

        PatientList list1 = new PatientList();
        list1.setUuid("11-22-33");
        list1.setName("Female Ward");
        list1.setDateCreated("2017-03-13T16:04:23.000+0300");
        list1.setBodyTemplate("<div class='body_template'></div>");
        list1.setHeaderTemplate("<div class='header_template'>#{p.identifier} {p.fullName} (Gender: {p.gender} - Age {p.age})</div>");

        PatientListCondition condition1 = new PatientListCondition();
        condition1.setConditionOrder(0);
        condition1.setField("p.gender");
        condition1.setOperator("EQUALS");
        condition1.setValue("F");
        List<PatientListCondition> conditions = new ArrayList<>();
        conditions.add(condition1);
        list1.setPatientListConditions(conditions);

        patientLists.add(list1);

        // populate patient list data
        patientListData = new ArrayList<>();
        PatientListContextModel data = new PatientListContextModel();
        data.setPatientList(list1);

        Patient patient = new Patient();
        Person person = new Person();
        person.setGender("Female");
        person.setBirthdate("2000-02-21");
        patient.setPerson(person);
        data.setPatient(patient);

        data.setHeaderContent("<div class='header_template'>#001 (Gender: Female - Age 17");
        data.setBodyContent("<div class='body_template'></div>");
        patientListData.add(data);
    }

    @Test
    public void shouldGetPatientList() throws Exception{
        PatientListCallbackListener listener = spy(PatientListCallbackListener.class);
        Call<Results<PatientList>> callSuccess = mockSuccessCall(patientLists);
        when(restService.getPatientLists()).thenReturn(callSuccess);
        patientListPresenter.getPatientList();
        verify(restService).getPatientLists();
        verify(patientListView).updatePatientLists(patientLists);
    }

    @Test
    public void shouldGetPatientListData() throws Exception{
        PatientListCallbackListener listener = spy(PatientListCallbackListener.class);
        Call<Results<PatientListContextModel>> callSuccess = mockSuccessCall(patientListData);
        when(restService.getPatientListData("11-22-33", 1, 10)).thenReturn(callSuccess);
        patientListPresenter.getPatientListData("11-22-33", 1, 10);
        verify(restService).getPatientListData("11-22-33", 1, 10);
        verify(patientListView).updatePatientListData(patientListData);
    }
}
