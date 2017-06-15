package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.base.Supplier;

import org.openmrs.mobile.data.cache.CacheService;
import org.openmrs.mobile.data.cache.SimpleCacheService;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.rest.RestServiceBuilder;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.models.Resource;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.Consumer;
import org.openmrs.mobile.utilities.Function;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.StringUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseDataService<E extends BaseOpenmrsObject, DS extends BaseDbService<E>, RS>
		implements DataService<E> {
	public static final String TAG = "BaseDataService";

	/**
	 * The database service for this entity.
	 */
	protected DS dbService;

	/**
	 * The REST service for this entity.
	 */
	protected RS restService;

	protected CacheService cacheService;

	private Class<E> entityClass;

	protected BaseDataService() {
		dbService = getDbService();
		restService = RestServiceBuilder.createService(getRestServiceClass());
		cacheService = SimpleCacheService.getInstance();
	}

	protected abstract DS getDbService();

	/**
	 * Gets the rest service class defined by the implementing class.
	 * @return The rest service class
	 */
	protected abstract Class<RS> getRestServiceClass();

	/**
	 * Gets the rest path for the specific entity.
	 * @return The rest path
	 */
	protected abstract String getRestPath();

	/**
	 * Gets the entity name used for rest calls for the specific entity.
	 * @return The entity name
	 */
	protected abstract String getEntityName();

	protected abstract Call<E> _restGetByUuid(String restPath, String uuid, QueryOptions options);

	protected abstract Call<Results<E>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo);

	protected abstract Call<E> _restCreate(String restPath, E entity);

	protected abstract Call<E> _restUpdate(String restPath, E entity);

	protected abstract Call<E> _restPurge(String restPath, String uuid);

	@Override
	public void getByUUID(@NonNull String uuid, @Nullable QueryOptions options, @NonNull GetCallback<E> callback) {
		checkNotNull(uuid);
		checkNotNull(callback);

		executeSingleCallback(callback, options,
				() -> dbService.getByUuid(uuid, options),
				() -> _restGetByUuid(buildRestRequestPath(), uuid, options));

		// Build local query
		// Build REST query

		// Check local for entity
		// If found, check for updates via rest
		// If not found, get from rest

		// Add to local
	}

	@Override
	public void getAll(@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@NonNull GetCallback<List<E>> callback) {
		checkNotNull(callback);

		executeMultipleCallback(callback, options, pagingInfo,
				() -> dbService.getAll(options, pagingInfo),
				() -> _restGetAll(buildRestRequestPath(), options, pagingInfo));
	}

	@Override
	public void search(@NonNull E template, @Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@NonNull GetCallback<List<E>> callback) {
		checkNotNull(template);
		checkNotNull(callback);

	}

	@Override
	public void create(@NonNull E entity, @NonNull GetCallback<E> callback) {
		checkNotNull(entity);
		checkNotNull(callback);

		executeSingleCallback(callback, null,
				() -> dbService.save(entity),
				() -> _restCreate(buildRestRequestPath(), entity));
	}

	@Override
	public void update(@NonNull E entity, @NonNull GetCallback<E> callback) {
		checkNotNull(entity);
		checkNotNull(callback);

		executeSingleCallback(callback, null,
				() -> dbService.save(entity),
				() -> _restUpdate(buildRestRequestPath(), entity));
	}

	@Override
	public void purge(@NonNull E entity, @NonNull VoidCallback callback) {
		checkNotNull(entity);
		checkNotNull(callback);

		executeVoidCallback(entity, callback, null,
				() -> dbService.delete(entity.getUuid()),
				() -> _restPurge(buildRestRequestPath(), entity.getUuid()));
	}

	/**
	 * Helper method to build the rest request path.
	 * @return The rest request path
	 */
	protected String buildRestRequestPath() {
		return getRestPath() + "/" + getEntityName();
	}

	/**
	 * Executes a data operation with no return result.
	 * @param callback      The operation callback
	 * @param dbOperation   The database operation to perform
	 * @param restOperation The REST operation to perform
	 */
	protected void executeVoidCallback(@Nullable E entity, @NonNull VoidCallback callback, @Nullable QueryOptions options,
			@NonNull Runnable dbOperation, @NonNull Supplier<Call<E>> restOperation) {
		checkNotNull(callback);
		checkNotNull(dbOperation);
		checkNotNull(restOperation);

		// Set up wrappers around callback and functions to work with performCallback method which assumes that something
		// will be returned
		GetCallback<E> callbackWrapper = new GetCallback<E>() {
			@Override
			public void onCompleted(E e) {
				callback.onCompleted();
			}

			@Override
			public void onError(Throwable t) {
				callback.onError(t);
			}
		};
		Supplier<E> dbSupplier = () -> {
			dbOperation.run();

			// Return the entity so the calling function knows that the operation completed successfully
			return entity;
		};
		Consumer<E> dbOperationWrapper = e -> dbOperation.run();

		performCallback(callbackWrapper, options, dbSupplier, restOperation,
				(E result) -> result, dbOperationWrapper);
	}

	/**
	 * Executes a data operation which returns a single result. Results returned from the REST query will be saved to the
	 * db.
	 * @param callback  The operation callback
	 * @param dbQuery   The database query operation to perform
	 * @param restQuery The REST query operation to perform
	 */
	protected void executeSingleCallback(@NonNull GetCallback<E> callback, @Nullable QueryOptions options,
			@NonNull Supplier<E> dbQuery, @NonNull Supplier<Call<E>> restQuery) {
		executeSingleCallback(callback, options, dbQuery, restQuery, (e) -> dbService.save(e));
	}

	/**
	 * Executes a data operation which returns a single result. The result returned from the REST request will be processed
	 * by the specified dbOperation.
	 * @param callback    The operation callback
	 * @param dbQuery     The database query operation to perform
	 * @param restQuery   The REST query operation to perform
	 * @param dbOperation The database operation to perform when results are returned from the REST query
	 */
	protected void executeSingleCallback(@NonNull GetCallback<E> callback, @Nullable QueryOptions options,
			@NonNull Supplier<E> dbQuery, @NonNull Supplier<Call<E>> restQuery, @NonNull Consumer<E> dbOperation) {
		checkNotNull(callback);
		checkNotNull(dbQuery);
		checkNotNull(restQuery);
		checkNotNull(dbOperation);

		performCallback(callback, options, dbQuery, restQuery,
				(E result) -> result,
				dbOperation);
	}

	/**
	 * Executes a data operation which returns a single result. The result returned from the REST request will be
	 * converted by the specified responseConverter function and then processed by the dbOperation.
	 * @param callback			The operation callback
	 * @param options			The query options
	 * @param dbQuery			The database query operation to perform
	 * @param restQuery			The REST query operation to perform
	 * @param responseConverter	The response converter function
	 * @param dbOperation		The database operation to perform when the results are returned from the REST query and
	 *                             converted by the response converter
	 * @param <T>				The entity type
	 * @param <R>				The type of the object returned by the REST query
	 */
	protected <T, R> void executeSingleCallback(@NonNull GetCallback<T> callback, @Nullable QueryOptions options,
			@NonNull Supplier<T> dbQuery, @NonNull Supplier<Call<R>> restQuery, @NonNull Function<R, T> responseConverter,
			@NonNull Consumer<T> dbOperation) {
		checkNotNull(callback);
		checkNotNull(dbQuery);
		checkNotNull(restQuery);
		checkNotNull(responseConverter);
		checkNotNull(dbOperation);

		performCallback(callback, options, dbQuery, restQuery, responseConverter, dbOperation);
	}

	/**
	 * Executes a data operation which can return multiple results. Results returned from the REST query will be saved to
	 * the db.
	 * @param callback   The operation callback
	 * @param pagingInfo The optional paging information
	 * @param dbQuery    The database query operation to perform
	 * @param restQuery  The REST query operation to perform
	 */
	protected void executeMultipleCallback(@NonNull GetCallback<List<E>> callback,
			@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@NonNull Supplier<List<E>> dbQuery, @NonNull Supplier<Call<Results<E>>> restQuery) {
		executeMultipleCallback(callback, options, pagingInfo, dbQuery, restQuery, (e) -> dbService.saveAll(e));
	}

	/**
	 * Executes a data operation which can return multiple results. The results returned from the REST request will be
	 * processed by the specified dbOperation.
	 * @param callback    The operation callback
	 * @param pagingInfo  The optional paging information
	 * @param dbQuery     The database query operation to perform
	 * @param restQuery   The REST query operation to perform
	 * @param dbOperation The database operation to perform when results are returned from the REST query
	 */
	protected void executeMultipleCallback(@NonNull GetCallback<List<E>> callback,
			@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@NonNull Supplier<List<E>> dbQuery, @NonNull Supplier<Call<Results<E>>> restQuery,
			@NonNull Consumer<List<E>> dbOperation) {
		checkNotNull(callback);
		checkNotNull(dbQuery);
		checkNotNull(restQuery);
		checkNotNull(dbOperation);

		performCallback(callback, options, dbQuery, restQuery,
				(Results<E> results) -> {
					if (pagingInfo != null) {
						pagingInfo.setTotalRecordCount(results.getLength());
					}

					return results.getResults();
				},
				dbOperation);
	}

	/**
	 * Executes the data operation (via REST or DB, depending on the network state) and performs the correct callback
	 * based on the operation result.
	 */
	private <T, R> void performCallback(GetCallback<T> callback, QueryOptions options, Supplier<T> dbSupplier,
			Supplier<Call<R>> restSupplier, Function<R, T> responseConverter, Consumer<T> dbSave) {
		if (!getCachedResult(callback, options)) {
			//if (!NetworkUtils.isServerAvailable()) {
			if (!NetworkUtils.isOnline()) {
				performOfflineCallback(callback, options, dbSupplier);
			} else {
				performOnlineCallback(callback, options, dbSupplier, restSupplier, responseConverter, dbSave);
			}
		}
	}

	/**
	 * Executes the specified data operation on the local db and performs the appropriate callback based on the operation
	 * result.
	 */
	private <T> void performOfflineCallback(GetCallback<T> callback, QueryOptions options, Supplier<T> dbSupplier) {
		// Perform the db task on another thread
		new Thread(() -> {
			try {
				// Try to get the entity from the db. If nothing is found just return null without any error
				T result = dbSupplier.get();

				setCachedResult(options, result);
				callback.onCompleted(result);
			} catch (Exception ex) {
				// An exception occurred while trying to get the entity from the db
				callback.onError(ex);
			}
		}).start();
	}

	/**
	 * Executes the specified data operation, first trying REST and then the local db if the REST request fails. The
	 * result of the REST operation, if successful, is processed by the specified dbOperation. If the REST operation fails
	 * because of a network issue (as determined by the error response code), then the local db is used instead. If the
	 * local db operation returns null then an exception is thrown to indicate that the call failed.
	 */
	private <T, R> void performOnlineCallback(GetCallback<T> callback, QueryOptions options, Supplier<T> dbSupplier,
			Supplier<Call<R>> restSupplier, Function<R, T> responseConverter, Consumer<T> dbOperation) {
		// Perform the rest task
		restSupplier.get().enqueue(new Callback<R>() {
			@Override
			public void onResponse(Call<R> call, Response<R> response) {
				// The request may have failed but still get here
				if (response.isSuccessful()) {
					// Save the resulting model to the db
					try {
						T result = responseConverter.apply(response.body());
						if (result instanceof Resource) {
							((Resource)result).processRelationships();
						}

						dbOperation.accept(result);

						setCachedResult(options, result);
						callback.onCompleted(result);
					} catch (Exception ex) {
						// An exception occurred while trying to save the entity in the db
						callback.onError(ex);
					}
				} else {
					// Something failed. If the issue was a connectivity issue then try to get the entity from the db
					if (response.code() >= 502 && response.code() <= 504) {
						new Thread(() -> {
							try {
								Log.w(TAG, "REST response error; trying local db (" + response.code() +
										": " + response.message() + "");
								T result = dbSupplier.get();

								if (result != null) {
									// Found it! Return this entity and ignore the rest connection error
									setCachedResult(options, response);
									callback.onCompleted(result);
								} else {
									// Could not find the entity so notify of the error
									callback.onError(
											new DataOperationException(response.code() + ": " + response.message()));
								}
							} catch (Exception ex) {
								// An exception occurred while trying to get the entity from the db
								callback.onError(new DataOperationException(ex));
							}
						}).start();
					} else {
						// Some other type of error occurred so just notify the caller about the error
 						callback.onError(new DataOperationException(response.code() + ": " + response.message()));
					}
				}
			}

			@Override
			public void onFailure(Call<R> call, Throwable t) {
				if (t instanceof IOException) {
					// Likely a connectivity issue so try to get from the db instead
					new Thread(() -> {
						try {
							Log.w(TAG, "REST response error; trying local db", t);
							T result = dbSupplier.get();

							if (result != null) {
								// Found it! Return this entity and ignore the rest exception
								setCachedResult(options, result);
								callback.onCompleted(result);
							} else {
								// Could not find the entity so notify of the error
								callback.onError(new DataOperationException(t));
							}
						} catch (Exception ex) {
							// An exception occurred while trying to get the entity from the db
							callback.onError(new DataOperationException(ex));
						}
					}).start();
				} else {
					// Some other type of exception occurred so just notify the caller about the exception
					Log.e(TAG, "REST request exception", t);
					callback.onError(new DataOperationException(t));
				}
			}
		});
	}

	protected <T> boolean getCachedResult(GetCallback<T> callback, QueryOptions options) {
		String cacheKey = QueryOptions.getCacheKey(options);
		if (StringUtils.notEmpty(cacheKey)) {
			Object cachedResult = cacheService.get(cacheKey);
			if (cachedResult != null) {
				callback.onCompleted((T)cachedResult);

				return true;
			}
		}

		return false;
	}

	protected void setCachedResult(QueryOptions options, Object value) {
		String cacheKey = QueryOptions.getCacheKey(options);
		if (StringUtils.notEmpty(cacheKey) && value != null) {
			cacheService.set(cacheKey, value);
		}
	}

	/**
	 * Gets a usable instance of the actual class of the generic type E defined by the implementing sub-class.
	 * @return The class object for the entity.
	 */
	@SuppressWarnings("unchecked")
	protected Class<E> getEntityClass() {
		if (entityClass == null) {
			ParameterizedType parameterizedType = (ParameterizedType)getClass().getGenericSuperclass();

			entityClass = (Class<E>)parameterizedType.getActualTypeArguments()[0];
		}

		return entityClass;
	}
}

