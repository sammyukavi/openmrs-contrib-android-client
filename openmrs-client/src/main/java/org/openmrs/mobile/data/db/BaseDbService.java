package org.openmrs.mobile.data.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.BaseTransformable;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;

import org.openmrs.mobile.data.DataOperationException;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.models.BaseOpenmrsObject;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseDbService<E extends BaseOpenmrsObject> implements DbService<E> {
	private Class<E> entityClass;

	protected abstract ModelAdapter<E> getEntityTable();

	@Override
	public List<E> getAll(@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo) {
		return executeQuery(options, pagingInfo, getEntityClass());
	}

	@Override
	public E getByUuid(@NonNull String uuid, @Nullable QueryOptions options) {
		checkNotNull(uuid);

		return SQLite.select()
				.from(getEntityClass())
				.where(getEntityTable().getProperty("uuid").eq(uuid))
				.querySingle();
	}

	@Override
	public List<E> saveAll(@NonNull List<E> entities) {
		checkNotNull(entities);

		FastStoreModelTransaction
				.saveBuilder(FlowManager.getModelAdapter(getEntityClass()))
				.addAll(entities)
				.build();

		return entities;
	}

	@Override
	public E save(@NonNull E entity) {
		checkNotNull(entity);

		if (FlowManager.getModelAdapter(getEntityClass()).save(entity)) {
			return entity;
		} else {
			throw new DataOperationException("Entity save failed.");
		}
	}

	@Override
	public void delete(@NonNull E entity) {
		checkNotNull(entity);
	}

	@Override
	public void delete(@NonNull String uuid) {
		checkNotNull(uuid);

		SQLite.delete(getEntityClass())
				.where(getEntityTable().getProperty("uuid").eq(uuid));
	}

	protected List<E> executeQuery(@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@Nullable Consumer<From<E>> where) {
		return executeQuery(options, pagingInfo, getEntityClass(), where);
	}

	protected <M> List<M> executeQuery(@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@NonNull Class<M> cls) {
		checkNotNull(cls);

		return executeQuery(options, pagingInfo, cls, null);
	}

	protected <M> List<M> executeQuery(@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@NonNull Class<M> cls, @Nullable Consumer<From<M>> where) {
		checkNotNull(cls);

		// Set up basic select query
		From<M> from = SQLite.select().from(cls);

		// Add paging logic, if defined
		if (PagingInfo.isValid(pagingInfo)) {
			// Check if paging total should be loaded
			if (pagingInfo.shouldLoadRecordCount()) {
				// Loading total record count
				From<M> pagingTotalQuery = SQLite.selectCountOf().from(cls);

				if (where != null) {
					where.accept(pagingTotalQuery);
				}

				pagingInfo.setTotalRecordCount((int)pagingTotalQuery.count());
			}

			// Set up paging logic
			from.limit(pagingInfo.getPageSize())
					.offset((pagingInfo.getPage() - 1) * pagingInfo.getPageSize());
		}

		// Add Where clauses, if defined
		if (where != null) {

			where.accept(from);
		}

		// Return the results
		return from.queryList();
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

