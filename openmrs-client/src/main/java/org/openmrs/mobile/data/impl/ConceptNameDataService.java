package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.rest.ConceptNameRestService;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.ConceptName;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class ConceptNameDataService extends BaseDataService<ConceptName, ConceptNameRestService> {

    @Override
    protected Class<ConceptNameRestService> getRestServiceClass() {
        return ConceptNameRestService.class;
    }

    @Override
    protected String getRestPath() {
        return ApplicationConstants.API.REST_ENDPOINT_V2 + "/patientlist/";
    }

    @Override
    protected String getEntityName() {
        return "conceptname";
    }

    @Override
    protected Call<ConceptName> _restGetByUuid(String restPath, String uuid, String representation) {
        return restService.getByUuid(restPath, uuid, representation);
    }

    public void getByConceptUuid(String conceptUuid, @NonNull GetMultipleCallback<ConceptName> callback) {
        executeMultipleCallback(callback, null, () -> {
            return restService.getByConceptUuid(buildRestRequestPath(), conceptUuid);
        });
    }

    @Override
    protected Call<Results<ConceptName>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation) {
        return null;
    }

    @Override
    protected Call<ConceptName> _restCreate(String restPath, ConceptName entity) {
        return null;
    }

    @Override
    protected Call<ConceptName> _restUpdate(String restPath, ConceptName entity) {
        return null;
    }

    @Override
    protected Call<ConceptName> _restPurge(String restPath, String uuid) {
        return null;
    }
}
