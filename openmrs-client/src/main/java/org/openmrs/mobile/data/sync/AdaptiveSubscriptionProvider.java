package org.openmrs.mobile.data.sync;

import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.RecordInfo;

import java.util.Date;
import java.util.List;

public abstract class AdaptiveSubscriptionProvider<E extends BaseOpenmrsObject> extends BaseSubscriptionProvider {
	protected DbService<E> dbService;

	protected abstract List<E> getRestAll();

	protected abstract List<RecordInfo> getRestRecordInfo();

	protected abstract E getRestObject(String uuid);

	@Override
	public void pull(PullSubscription subscription) {
		long recordCount = dbService.getCount(null);

		if (recordCount == 0) {
			// If table is empty then do a table pull
			pullTable(subscription.getLastSync(), false);
		} else {
			pullIncremental(subscription.getLastSync());
		}
	}

	protected void pullTable(Date since, boolean processDeletions) {
		// Get all records via rest
		List<E> table = getRestAll();

		if (processDeletions) {
			// Delete local records that are not found
		}

		// Update local records
		// Create new local records
	}

	protected void pullIncremental(Date since) {
		// Get the record info (UUID, DateUpdated) for each record via REST
		List<RecordInfo> recordInfo = getRestRecordInfo();

		// Insert record info into temp table

		// Delete local records that are not in record info

		// Calculate local records to update
		// Calculate new local records to create

		// If update+new count is greater than max incremental update then do table pull

		// Else pull each record via rest and save to the local db
	}

	protected void processDeletions(List<E> table) {

	}

	protected void processUpdates(List<E> table) {

	}

	protected void processNew(List<E> table) {

	}

	protected void processDeletions() {

	}

	protected List<String> calculateUpdates() {
		return null;
	}

	protected List<String> calculateNew() {
		return null;
	}
}
