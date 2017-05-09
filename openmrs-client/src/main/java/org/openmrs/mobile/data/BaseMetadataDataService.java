package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.BaseOpenmrsMetadata;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseMetadataDataService<E extends BaseOpenmrsMetadata, S> extends BaseDataService<E, S>
		implements MetadataDataService<E> {

	protected abstract Call<Results<E>> _restGetByNameFragment(String restPath, PagingInfo pagingInfo, String name,
			String representation);

	@Override
	public void getByNameFragment(@NonNull String name, boolean includeInactive,
			@Nullable PagingInfo pagingInfo,
			@NonNull GetMultipleCallback<E> callback) {
		checkNotNull(name);
		checkNotNull(callback);

		executeMultipleCallback(callback, pagingInfo,
				() -> _restGetByNameFragment(buildRestRequestPath(), pagingInfo, name, RestConstants.Representations.FULL));
	}
}
