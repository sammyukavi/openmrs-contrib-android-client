package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.base.Supplier;

import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.rest.RestServiceBuilder;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.NetworkUtils;

import java.io.IOException;
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

	protected BaseDataService() {
		dbService = getDbService();
		restService = RestServiceBuilder.createService(getRestServiceClass());
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

		executeSingleCallback(callback,
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

		executeMultipleCallback(callback, pagingInfo,
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

		executeSingleCallback(callback,
				() -> dbService.save(entity),
				() -> _restCreate(buildRestRequestPath(), entity));
	}

	@Override
	public void update(@NonNull E entity, @NonNull GetCallback<E> callback) {
		checkNotNull(entity);
		checkNotNull(callback);

		executeSingleCallback(callback,
				() -> dbService.save(entity),
				() -> _restUpdate(buildRestRequestPath(), entity));
	}

	@Override
	public void purge(@NonNull E entity, @NonNull VoidCallback callback) {
		checkNotNull(entity);
		checkNotNull(callback);

		executeVoidCallback(entity, callback,
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
	protected void executeVoidCallback(@Nullable E entity, @NonNull VoidCallback callback,
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

		performCallback(callbackWrapper, dbSupplier, restOperation,
				(E result) -> result, dbOperationWrapper);
	}

	/**
	 * Executes a data operation which returns a single result. Results returned from the REST query will be saved to the
	 * db.
	 * @param callback  The operation callback
	 * @param dbQuery   The database query operation to perform
	 * @param restQuery The REST query operation to perform
	 */
	protected void executeSingleCallback(@NonNull GetCallback<E> callback,
			@NonNull Supplier<E> dbQuery, @NonNull Supplier<Call<E>> restQuery) {
		executeSingleCallback(callback, dbQuery, restQuery, (e) -> dbService.save(e));
	}

	/**
	 * Executes a data operation which returns a single result.
	 * @param callback    The operation callback
	 * @param dbQuery     The database query operation to perform
	 * @param restQuery   The REST query operation to perform
	 * @param dbOperation The database operation to perform when results are returned from the REST query
	 */
	protected void executeSingleCallback(@NonNull GetCallback<E> callback,
			@NonNull Supplier<E> dbQuery, @NonNull Supplier<Call<E>> restQuery, @NonNull Consumer<E> dbOperation) {
		checkNotNull(callback);
		checkNotNull(dbQuery);
		checkNotNull(restQuery);
		checkNotNull(dbOperation);

		performCallback(callback, dbQuery, restQuery,
				(E result) -> result,
				dbOperation);
	}

	/**
	 * Executes a data operation which can return multiple results. Results returned from the REST query will be saved to
	 * the db.
	 * @param callback   The operation callback
	 * @param pagingInfo The optional paging information
	 * @param dbQuery    The database query operation to perform
	 * @param restQuery  The REST query operation to perform
	 */
	protected void executeMultipleCallback(@NonNull GetCallback<List<E>> callback, @Nullable PagingInfo pagingInfo,
			@NonNull Supplier<List<E>> dbQuery, @NonNull Supplier<Call<Results<E>>> restQuery) {
		executeMultipleCallback(callback, pagingInfo, dbQuery, restQuery, (e) -> dbService.saveAll(e));
	}

	/**
	 * Executes a data operation which can return multiple results.
	 * @param callback    The operation callback
	 * @param pagingInfo  The optional paging information
	 * @param dbQuery     The database query operation to perform
	 * @param restQuery   The REST query operation to perform
	 * @param dbOperation The database operation to perform when results are returned from the REST query
	 */
	protected void executeMultipleCallback(@NonNull GetCallback<List<E>> callback, @Nullable PagingInfo pagingInfo,
			@NonNull Supplier<List<E>> dbQuery, @NonNull Supplier<Call<Results<E>>> restQuery,
			@NonNull Consumer<List<E>> dbOperation) {
		checkNotNull(callback);
		checkNotNull(dbQuery);
		checkNotNull(restQuery);
		checkNotNull(dbOperation);

		performCallback(callback, dbQuery, restQuery,
				(Results<E> results) -> {
					if (pagingInfo != null) {
						pagingInfo.setTotalRecordCount(results.getLength());
					}

					return results.getResults();
				},
				dbOperation);
	}

	private <T, R> void performCallback(GetCallback<T> callback, Supplier<T> dbSupplier,
			Supplier<Call<R>> restSupplier, Function<R, T> responseConverter, Consumer<T> dbSave) {
		//if (!NetworkUtils.isServerAvailable()) {
		if (!NetworkUtils.isOnline()) {
			performOfflineCallback(callback, dbSupplier);
		} else {
			performOnlineCallback(callback, dbSupplier, restSupplier, responseConverter, dbSave);
		}
	}

	private <T> void performOfflineCallback(GetCallback<T> callback, Supplier<T> dbSupplier) {
		// Perform the db task on another thread
		new Thread(() -> {
			try {
				// Try to get the entity from the db. If nothing is found just return null without any error
				callback.onCompleted(dbSupplier.get());
			} catch (Exception ex) {
				// An exception occurred while trying to get the entity from the db
				callback.onError(ex);
			}
		}).start();
	}

	private <T, R> void performOnlineCallback(GetCallback<T> callback, Supplier<T> dbSupplier,
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

						dbOperation.accept(result);
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

	protected boolean isPagingValid(PagingInfo pagingInfo) {
		return !(pagingInfo == null || pagingInfo.getPage() == 0);
	}

	private interface Consumer<T> {
		void accept(T value);
	}

	private interface Function<R, T> {
		T apply(R value);
	}
}

