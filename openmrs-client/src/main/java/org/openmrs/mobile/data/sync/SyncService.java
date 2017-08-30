package org.openmrs.mobile.data.sync;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.openmrs.mobile.data.DataOperationException;
import org.openmrs.mobile.data.db.impl.PullSubscriptionDbService;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.event.SyncEvent;
import org.openmrs.mobile.event.SyncPullEvent;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.SyncLog;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class SyncService {
	private static final String TAG = SyncService.class.getSimpleName();

	private static final Object SYNC_LOCK = new Object();

	private SyncLogDbService syncLogDbService;
	private PullSubscriptionDbService subscriptionDbService;
	private DaggerProviderHelper providerHelper;
	private EventBus eventBus;

	private NetworkUtils networkUtils;

	@Inject
	public SyncService(SyncLogDbService syncLogDbService, PullSubscriptionDbService subscriptionDbService,
			DaggerProviderHelper providerHelper, NetworkUtils networkUtils, EventBus eventBus) {
		this.syncLogDbService = syncLogDbService;
		this.subscriptionDbService = subscriptionDbService;
		this.providerHelper = providerHelper;
		this.networkUtils = networkUtils;
		this.eventBus = eventBus;
	}

	private Map<String, SubscriptionProvider> subscriptionProviders = new HashMap<String, SubscriptionProvider>();

	public void sync() {
		// Synchronize access so that only one thread is synchronizing at a time
		synchronized (SYNC_LOCK) {
			// Push changes to server
			push();

			// Pull subscription changes
			pull();
		}
	}

	protected void push() {
		List<SyncLog> records = syncLogDbService.getAll(null, null);

		/*
		for (SyncLog record : records) {
			syncProvider.sync(record);

			syncLogDbService.delete(record);
		}
		*/
	}

	/**
	 * Processes all defined subscription providers, ensuring that they are only processed when the minimum interval has
	 * elapsed.
	 */
	protected void pull() {
		// Get subscriptions
		List<PullSubscription> subscriptions = subscriptionDbService.getAll(null, null);
		eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.TOTAL_SUBSCRIPTIONS, "subscriptions",
				subscriptions.size()));
		for (PullSubscription sub : subscriptions) {
			eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.SUBSCRIPTION_REMOTE_PULL_STARTING,
					sub.getSubscriptionClass(), null));
			// Check if subscription should be processed, given the minimum interval
			Integer seconds = null;
			if (sub.getLastSync() != null && sub.getMinimumInterval() != null) {
				Period p = new Period(new DateTime(sub.getLastSync()), DateTime.now());
				seconds = p.getSeconds();
			}

			if (seconds == null || sub.getMinimumInterval() == null || seconds > sub.getMinimumInterval()) {
				// Try to get the cached subscription provider
				SubscriptionProvider provider = subscriptionProviders.get(sub.getSubscriptionClass());
				if (provider == null) {
					provider = providerHelper.getSubscriptionProvider(sub.getSubscriptionClass());

					subscriptionProviders.put(sub.getSubscriptionClass(), provider);
				}

				// If the provider was instantiated then execute it
				if (provider != null) {
					try {
						// Get the date before starting the pull process so that server changes while the provider is
						// processing don't get lost
						Date lastSync = new Date();

						provider.initialize(sub);
						provider.pull(sub);

						sub.setLastSync(lastSync);
					} catch (DataOperationException doe) {
						Log.w(TAG, "Data exception occurred while processing subscription provider '" +
										sub.getSubscriptionClass() + ":" +
										(StringUtils.isBlank(sub.getSubscriptionKey()) ? "(null)" :
												sub.getSubscriptionKey()) + "'", doe);

						// Check to see if we're still online, if not, then stop the sync
						if (!networkUtils.hasNetwork()) {
							eventBus.post(new SyncEvent(ApplicationConstants.EventMessages.Sync.CANT_SYNC_NO_NETWORK,
									null, null));
							break;
						}
					} catch (Exception ex) {
						Log.e(TAG, "An exception occurred while processing subscription provider '" +
										sub.getSubscriptionClass() + ":" +
										(StringUtils.isBlank(sub.getSubscriptionKey()) ? "(null)" :
												sub.getSubscriptionKey()) + "'", ex);
					}
				}
			}

			eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.SUBSCRIPTION_REMOTE_PULL_COMPLETE,
					sub.getSubscriptionClass(), null));
		}
	}
}

