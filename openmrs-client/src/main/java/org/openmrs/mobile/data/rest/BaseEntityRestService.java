package org.openmrs.mobile.data.rest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.models.BaseOpenmrsEntity;
import org.openmrs.mobile.models.Results;

import java.lang.reflect.Method;

import retrofit2.Call;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseEntityRestService<E extends BaseOpenmrsEntity, RS> extends BaseRestService<E, RS>
		implements EntityRestService<E> {
	public static final String GET_BY_PATIENT_METHOD_NAME = "getByPatient";

	private Method getByPatientMethod;

	protected BaseEntityRestService() {
		super();

		initializeRestMethods();
	}

	@Override
	public Call<Results<E>> getByPatient(@NonNull String patientUuid, @Nullable QueryOptions options,
			@Nullable PagingInfo pagingInfo) {
		checkNotNull(patientUuid);

		if (getByPatientMethod == null) {
			return null;
		}

		Call<Results<E>> call = null;

		try {
			Object result = getByPatientMethod.invoke(restService, buildRestRequestPath(), patientUuid,
					QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
					PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo));

			if (result != null) {
				call = (Call<Results<E>>)result;
			}
		} catch (Exception nex) {
			call = null;
		}

		return call;
	}

	private void initializeRestMethods() {
		Class<?> restClass = restService.getClass();

		if (getByPatientMethod == null) {
			try {
				getByPatientMethod = restClass.getMethod(GET_BY_PATIENT_METHOD_NAME);
			} catch (Exception ignored) {
			}
		}
	}
}

