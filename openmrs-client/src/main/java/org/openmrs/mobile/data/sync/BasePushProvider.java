package org.openmrs.mobile.data.sync;

import org.openmrs.mobile.data.DataOperationException;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.DbService;
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
		DS extends DbService<E>, RS extends RestService<E>> implements SyncProvider {

	private SyncLogDbService syncLogDbService;
	private DS dbService;
	private RS restService;

	public BasePushProvider(SyncLogDbService syncLogDbService, DS dbService, RS restService) {
		this.syncLogDbService = syncLogDbService;
		this.dbService = dbService;
		this.restService = restService;
	}

	@Override
	public void sync(SyncLog record) {
		push(record);
	}

	protected void push(SyncLog syncLog) {
		E entity = dbService.getByUuid(syncLog.getKey(), QueryOptions.FULL_REP);
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

		syncLogDbService.delete(syncLog);
	}
}
