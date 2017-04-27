package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.models.BaseOpenmrsMetadata;

/**
 * Represents classes the provide data services for {@link BaseOpenmrsMetadata} objects.
 * @param <E> The entity class
 */
public interface MetadataDataService<E extends BaseOpenmrsMetadata> extends DataService<E> {
    /**
     * Gets entities with the specified name fragment.
     * @param name The name to search for
     * @param includeInactive {@code true} to include inactive entities; otherwise, {@code false}
     * @param pagingInfo The paging information or null to exclude paging
     * @param callback
     */
    void getByNameFragment(@NonNull String name, boolean includeInactive,
                           @Nullable PagingInfo pagingInfo,
                           @NonNull GetMultipleCallback<E> callback);
}
