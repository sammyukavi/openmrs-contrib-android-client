package org.openmrs.mobile.data;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class DataUtil {
	private Repository repository;

	@Inject
	public DataUtil(Repository repository) {
		this.repository = repository;
	}

	/**
	 * Deletes the records from the specified model's table that are not found in the expected records. Records are compared
	 * using only the uuid values.
	 * @param model The model table to delete the records from
	 * @param expectedRecords The records that should exist in the table
	 * @param <T> The model type
	 */
	public <T extends Resource> void diffDelete(@NonNull Class<T> model, @NonNull List<T> expectedRecords) {
		checkNotNull(model);
		checkNotNull(expectedRecords);

		ModelAdapter<T> table = FlowManager.getModelAdapter(model);

		if (expectedRecords.size() == 0) {
			// Delete all records from source table
			repository.deleteAll(table);
		} else {
			// Get the uuid's of the current source records
			List<String> existingRecords = repository.queryCustom(String.class, table, table.getProperty("uuid"));
			if (existingRecords == null || existingRecords.size() == 0) {
				// There are no records so there is nothing to delete
				return;
			}

			// Put the record uuid's into a map for searching
			Map<String, String> recordMap = new HashMap<>(expectedRecords.size());
			for (T record : expectedRecords) {
				recordMap.put(record.getUuid(), "");
			}

			// Find the uuid's that are not in the record list
			List<String> deletes = new ArrayList<>();
			for (String uuid : existingRecords) {
				if (!recordMap.containsKey(uuid)) {
					deletes.add(uuid);
				}
			}

			if (deletes.size() > 0) {
				// Delete the source records that were not found
				repository.deleteAll(table, table.getProperty("uuid").in(deletes));
			}
		}
	}
}
