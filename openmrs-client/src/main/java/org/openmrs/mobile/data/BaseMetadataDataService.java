package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.db.BaseMetadataDbService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.BaseOpenmrsMetadata;
import org.openmrs.mobile.models.Results;

import java.util.List;

import retrofit2.Call;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseMetadataDataService<E extends BaseOpenmrsMetadata, DS extends BaseMetadataDbService<E>, RS>
        extends BaseDataService<E, DS, RS> implements MetadataDataService<E> {
    protected abstract Call<Results<E>> _restGetByNameFragment(String restPath, PagingInfo pagingInfo, String name,
                                                               String representation);

    @Override
    public void getByNameFragment(@NonNull String name, boolean includeInactive,
                                  @Nullable PagingInfo pagingInfo,
                                  @NonNull GetCallback<List<E>> callback) {
        checkNotNull(name);
        checkNotNull(callback);

        executeMultipleCallback(callback,
                () -> dbService.getByNameFragment(name, includeInactive, pagingInfo),
                () -> _restGetByNameFragment(buildRestRequestPath(), pagingInfo, name, RestConstants.Representations.FULL));
    }
}
