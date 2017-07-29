package org.openmrs.mobile.data.sync;

import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.Results;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public abstract class BaseSubscriptionProvider implements SubscriptionProvider {
	@Override
	public void initialize(PullSubscription subscription) { }

	@Override
	public abstract void pull(PullSubscription subscription);

	protected <T> List<T> getCallListValue(Call<Results<T>> call) {
		if (call == null) {
			return null;
		}

		Response<Results<T>> response;
		try {
			response = call.execute();
		} catch (Exception ex) {
			response = null;
		}

		List<T> result = null;
		if (response != null) {
			Results<T> temp = response.body();

			if (temp != null) {
				result = temp.getResults();
			}
		}

		return result;
	}

	protected <T> T getCallValue(Call<T> call) {
		if (call == null) {
			return null;
		}

		Response<T> response;
		try {
			response = call.execute();
		} catch (Exception ex) {
			response = null;
		}

		T result = null;
		if (response != null) {
			result = response.body();
		}

		return result;
	}
}
