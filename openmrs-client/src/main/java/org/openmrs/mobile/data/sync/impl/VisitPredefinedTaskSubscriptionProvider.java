package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.RecordInfoDbService;
import org.openmrs.mobile.data.db.impl.VisitPredefinedTaskDbService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.impl.VisitPredefinedTaskRestServiceImpl;
import org.openmrs.mobile.data.sync.AdaptiveSubscriptionProvider;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.VisitPredefinedTask;

import java.util.List;

import javax.inject.Inject;

public class VisitPredefinedTaskSubscriptionProvider extends AdaptiveSubscriptionProvider<VisitPredefinedTask,
		VisitPredefinedTaskDbService, VisitPredefinedTaskRestServiceImpl> {
	VisitPredefinedTaskDbService visitPredefinedTaskDbService;
	List<VisitPredefinedTask> visitPredefinedTaskList;
	private QueryOptions options = new QueryOptions();

	@Inject
	public VisitPredefinedTaskSubscriptionProvider(VisitPredefinedTaskDbService dbService,
			RecordInfoDbService recordInfoDbService,
			VisitPredefinedTaskRestServiceImpl restService, Repository repository) {
		super(dbService, recordInfoDbService, restService, repository);

		this.visitPredefinedTaskDbService = dbService;
		options.setCustomRepresentation(RestConstants.Representations.VIST_PREDEFINED_TASKS);
	}

	@Override
	public void initialize(PullSubscription subscription) {
		super.initialize(subscription);
		visitPredefinedTaskList = visitPredefinedTaskDbService.getAll(options, null);
		saveAllDb(visitPredefinedTaskList);
	}

	@Override
	protected long getRecordCountDb() {
		return visitPredefinedTaskDbService.getCount(options);
	}

	@Override
	protected void saveAllDb(List<VisitPredefinedTask> entities) {
		// First save the predefined tasks on the predefined tasks table
		super.saveAllDb(entities);
	}

	@Override
	protected List<VisitPredefinedTask> getAllRest() {
		return RestHelper.getCallListValue(restService.getAll(options, null));
	}

	@Override
	protected List<RecordInfo> getRecordInfoRest() {
		return RestHelper.getCallListValue(
				restService.getSetVisitPredefinedTasksRecordInfo(options));
	}
}
