package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.db.BaseEntityDbService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.BaseOpenmrsEntity;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Results;

import java.util.List;

import retrofit2.Call;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseEntityDataService<E extends BaseOpenmrsEntity, DS extends BaseEntityDbService<E>, RS>
        extends BaseDataService<E, DS, RS> implements EntityDataService<E> {
    protected abstract Call<Results<E>> _restGetByPatient(String restPath, PagingInfo pagingInfo, String patientUuid,
                                                          String representation);

    @Override
    public void getByPatient(@NonNull Patient patient, boolean includeInactive, @Nullable PagingInfo pagingInfo,
                             @NonNull GetCallback<List<E>> callback) {
        checkNotNull(patient);
        checkNotNull(callback);

        executeMultipleCallback(callback,
                () -> dbService.getByPatient(patient, includeInactive, pagingInfo),
                () -> _restGetByPatient(buildRestRequestPath(), pagingInfo, patient.getUuid(), RestConstants.Representations.FULL));
    }
}
