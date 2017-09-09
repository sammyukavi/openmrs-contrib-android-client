package org.openmrs.mobile.data.sync;

import android.util.Log;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DataOperationException;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.RestService;
import org.openmrs.mobile.data.sync.SyncProvider;
import org.openmrs.mobile.models.BaseOpenmrsAuditableObject;
import org.openmrs.mobile.models.SyncLog;
import org.openmrs.mobile.utilities.StringUtils;

import retrofit2.Call;

/**
 * Base class for push providers
 */
public abstract class BasePushProvider<E extends BaseOpenmrsAuditableObject,
		DS extends DbService<E>, RS extends RestService<E>> implements SyncProvider {

	private static final String TAG = BasePushProvider.class.getSimpleName();
	private SyncLogDbService syncLogDbService;
	private DS dbService;
	private RS restService;
	private OpenMRS openMRS;

	public BasePushProvider(SyncLogDbService syncLogDbService, DS dbService, RS restService, OpenMRS openMRS) {
		this.syncLogDbService = syncLogDbService;
		this.dbService = dbService;
		this.restService = restService;
		this.openMRS = openMRS;
	}

	@Override
	public void sync(SyncLog record) {
		push(record);
	}

	protected void push(SyncLog syncLog) {
		if(StringUtils.notNull(openMRS.getPatientUuid()) && openMRS.getPatientUuid().equalsIgnoreCase(syncLog.getKey())) {
			Log.i(TAG, "Skip. The Patient with uuid '" + syncLog.getKey() + "' is currently being viewed");
			return;
		}

		if(StringUtils.notNull(openMRS.getVisitUuid()) && openMRS.getVisitUuid().equalsIgnoreCase(syncLog.getKey())) {
			Log.i(TAG, "Skip. The Visit with uuid '" + syncLog.getKey() + "' is currently being viewed");
			return;
		}

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
