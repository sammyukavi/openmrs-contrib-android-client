package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.db.BaseEntityDbService;
import org.openmrs.mobile.models.BaseOpenmrsEntity;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Results;

import java.util.List;

import retrofit2.Call;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseEntityDataService<E extends BaseOpenmrsEntity, DS extends BaseEntityDbService<E>, RS>
		extends BaseDataService<E, DS, RS> implements EntityDataService<E> {
	protected abstract Call<Results<E>> _restGetByPatient(String restPath, String patientUuid, QueryOptions options,
			PagingInfo pagingInfo);

	@Override
	public void getByPatient(@NonNull Patient patient, @Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@NonNull GetCallback<List<E>> callback) {
		checkNotNull(patient);
		checkNotNull(callback);

		executeMultipleCallback(callback, pagingInfo,
				() -> dbService.getByPatient(patient, options, pagingInfo),
				() -> _restGetByPatient(buildRestRequestPath(), patient.getUuid(), options, pagingInfo));
	}
}
