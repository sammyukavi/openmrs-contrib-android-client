package org.openmrs.mobile.data.db.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import java.util.Date;

import android.support.annotation.NonNull;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.EntitySyncInfo;
import org.openmrs.mobile.models.EntitySyncInfo_Table;
import org.openmrs.mobile.models.Patient;

public class EntitySyncInfoDbService extends BaseDbService<EntitySyncInfo> {
	@Inject
	public EntitySyncInfoDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<EntitySyncInfo> getEntityTable() {
		return (EntitySyncInfo_Table)FlowManager.getInstanceAdapter(EntitySyncInfo.class);
	}

	public EntitySyncInfo getPatientLastSyncInfo(@NonNull String patientUuid) {
		checkNotNull(patientUuid);

		return repository.querySingle(getEntityTable(), EntitySyncInfo_Table.entityClass.eq(Patient.class.getSimpleName()),
				EntitySyncInfo_Table.entityUuid.eq(patientUuid));
	}

	public void savePatientLastSyncInfo(@NonNull String patientUuid, @NonNull Date lastSync) {
		checkNotNull(patientUuid);
		checkNotNull(lastSync);

		EntitySyncInfo entitySyncInfo = getPatientLastSyncInfo(patientUuid);
		if (entitySyncInfo == null) {
			entitySyncInfo = new EntitySyncInfo();
			entitySyncInfo.setEntityClass(Patient.class.getSimpleName());
			entitySyncInfo.setEntityUuid(patientUuid);
		}

		entitySyncInfo.setLastSync(lastSync);

		repository.save(getEntityTable(), entitySyncInfo);
	}
}
