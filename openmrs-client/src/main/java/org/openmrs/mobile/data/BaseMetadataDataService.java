package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.db.BaseMetadataDbService;
import org.openmrs.mobile.models.BaseOpenmrsMetadata;
import org.openmrs.mobile.models.Results;

import java.util.List;

import retrofit2.Call;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseMetadataDataService<E extends BaseOpenmrsMetadata, DS extends BaseMetadataDbService<E>, RS>
		extends BaseDataService<E, DS, RS> implements MetadataDataService<E> {
	protected abstract Call<Results<E>> _restGetByNameFragment(String restPath, String name, QueryOptions options,
			PagingInfo pagingInfo);

	@Override
	public void getByNameFragment(@NonNull String name, @Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@NonNull GetCallback<List<E>> callback) {
		checkNotNull(name);
		checkNotNull(callback);

		executeMultipleCallback(callback, pagingInfo,
				() -> dbService.getByNameFragment(name, options, pagingInfo),
				() -> _restGetByNameFragment(buildRestRequestPath(), name, options, pagingInfo));
	}
}
