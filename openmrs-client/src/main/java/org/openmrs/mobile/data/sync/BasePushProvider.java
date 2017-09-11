package org.openmrs.mobile.data.sync;

import org.openmrs.mobile.data.DataOperationException;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.RestService;
import org.openmrs.mobile.models.BaseOpenmrsAuditableObject;
import org.openmrs.mobile.models.SyncLog;

import retrofit2.Call;

/**
 * Base class for push providers
 */
public abstract class BasePushProvider<E extends BaseOpenmrsAuditableObject,
		DS extends DbService<E>, RS extends RestService<E>> implements PushProvider {
	protected DS dbService;
	protected RS restService;

	public BasePushProvider(DS dbService, RS restService) {
		this.dbService = dbService;
		this.restService = restService;
	}

	protected abstract void deleteLocalRelatedRecords(E originalEntity, E restEntity);

	@Override
	public void push(SyncLog syncLog) {
		E entity = dbService.getByUuid(syncLog.getKey(), QueryOptions.FULL_REP);
		if(entity == null) {
			throw new DataOperationException("Entity not found");
		}

		// Prepare rest call
		Call<E> call = null;
		switch (syncLog.getAction()) {
			case NEW:
				call = restService.create(entity);
				break;
			case UPDATED:
				call = restService.update(entity);
				break;
			case DELETED:
				call = restService.purge(entity.getUuid());
				break;
		}

		// Perform rest call
		E restEntity = RestHelper.getCallValue(call);
		if (restEntity != null) {
			// Delete any related records with local uuids, records with the server-generated uuids will be saved when
			// saving the entity below
			deleteLocalRelatedRecords(entity, restEntity);

			// Check if uuid has changed
			if (!entity.getUuid().equalsIgnoreCase(restEntity.getUuid())) {
				// This will update the uuid for the current entity and also save any related objects
				dbService.update(entity.getUuid(), restEntity);
			}
		}
	}
}
