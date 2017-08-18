package org.openmrs.mobile.data.sync;

import org.openmrs.mobile.data.DataOperationException;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.RestService;
import org.openmrs.mobile.data.sync.SyncProvider;
import org.openmrs.mobile.models.BaseOpenmrsAuditableObject;
import org.openmrs.mobile.models.SyncLog;

import retrofit2.Call;

/**
 * Base class for push providers
 */
public abstract class BasePushProvider<E extends BaseOpenmrsAuditableObject,
		DS extends DbService<E>, RS extends RestService<E>> implements SyncProvider {

	private SyncLogDbService syncLogDbService;
	private DS dbService;
	private RS restService;

	public BasePushProvider(SyncLogDbService syncLogDbService, DS dbService, RS restService) {
		this.syncLogDbService = syncLogDbService;
		this.dbService = dbService;
		this.restService = restService;
	}

	private E getEntity(String uuid) {
		return dbService.getByUuid(uuid, null);
	}

	protected void push(SyncLog syncLog) {
		E entity = getEntity(syncLog.getKey());
		if(entity == null) {
			throw new DataOperationException("Entity not found");
		}

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

		// perform rest call
		RestHelper.getCallValue(call);

		deleteSyncLog(syncLog);

	}

	private void deleteSyncLog(SyncLog entity) {
		syncLogDbService.delete(entity);
	}
}
