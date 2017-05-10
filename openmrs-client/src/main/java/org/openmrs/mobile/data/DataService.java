package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.models.BaseOpenmrsObject;

import java.util.List;

/**
 * Represents classes that provide data services for {@link BaseOpenmrsObject} objects.
 * @param <E> The entity class
 */
public interface DataService<E extends BaseOpenmrsObject> {
	/**
	 * Gets a single entity with the specified UUID.
	 * @param uuid     The entity UUID
	 * @param callback
	 */
	void getByUUID(@NonNull String uuid, @NonNull GetSingleCallback<E> callback);

	/**
	 * Gets all entities.
	 * @param includeInactive {@code true} to include inactive entities; otherwise, {@code false}
	 * @param pagingInfo      The paging information or null to exclude paging
	 * @param callback
	 */
	void getAll(boolean includeInactive, @Nullable PagingInfo pagingInfo,
			@NonNull GetMultipleCallback<E> callback);

	/**
	 * Performs a template-based search. Doesn't actually work at this point, unless you search
	 * for something which should return no results.
	 * @param template   The entity template to create the search values from
	 * @param pagingInfo The paging information or null to exclude paging
	 * @param callback
	 */
	void search(@NonNull E template, @Nullable PagingInfo pagingInfo,
			@NonNull GetMultipleCallback<E> callback);

	/**
	 * Saves a newly created entity.
	 * @param entity   The new entity to save
	 * @param callback
	 */
	void create(@NonNull E entity, @NonNull GetSingleCallback<E> callback);

	/**
	 * Saves an updated entity.
	 * @param entity   The entity to save
	 * @param callback
	 */
	void update(@NonNull E entity, @NonNull GetSingleCallback<E> callback);

	/**
	 * Completely deletes the entity.
	 * @param entity   The entity to purge
	 * @param callback
	 */
	void purge(@NonNull E entity, @NonNull VoidCallback callback);

	/**
	 * Callback interface for operations with no return value.
	 */
	interface VoidCallback {
		/**
		 * Called if the operation completes successfully.
		 */
		void onCompleted();

		/**
		 * Called if the operation fails.
		 * @param t The exception information
		 */
		void onError(Throwable t);
	}

	/**
	 * Callback interface for operations that return a single entity.
	 * @param <E> The entity class
	 */
	interface GetSingleCallback<E> {
		/**
		 * Called if the operation completes successfully.
		 * @param entity The returned entity
		 */
		void onCompleted(E entity);

		/**
		 * Called if the operation fails.
		 * @param t The exception information
		 */
		void onError(Throwable t);
	}

	/**
	 * Callback interface for operations that return multiple entities.
	 * @param <E> The entity class
	 */
	interface GetMultipleCallback<E> {
		/**
		 * Called if the operation completes successfully.
		 * @param entities The returned entities
		 */
		void onCompleted(List<E> entities, int length);

		/**
		 * Called if the operation fails.
		 * @param t The exception information
		 */
		void onError(Throwable t);
	}
}

