package org.openmrs.mobile.data.db.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.ConceptName;
import org.openmrs.mobile.models.ConceptName_Table;
import org.openmrs.mobile.models.Concept_Table;

import java.util.List;

import javax.inject.Inject;

public class ConceptDbService extends BaseDbService<Concept> implements DbService<Concept> {
	@Inject
	public ConceptDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<Concept> getEntityTable() {
		return (Concept_Table)FlowManager.getInstanceAdapter(Concept.class);
	}

	public List<Concept> getByName(@NonNull String conceptName, @Nullable QueryOptions options) {
		return null;
	}

	public List<Concept> findConcept(@NonNull String name, @Nullable QueryOptions options) {
		if (!name.startsWith("%")) {
			name = "%" + name;
		}
		if (!name.endsWith("%")) {
			name = name + "%";
		}

		final String search = name;

		return executeQuery(options, null, (f) -> f.where(searchByName(search)));
	}

	private SQLOperator searchByName(String name) {
		return Concept_Table.name_uuid.in(
				SQLite.select(ConceptName_Table.uuid)
						.from(ConceptName.class)
						.where(ConceptName_Table.name.like(name))
		);
	}
}
