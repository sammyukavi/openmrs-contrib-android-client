package org.openmrs.mobile.activities.patientdashboard;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.impl.ObsDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

public class VisitsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private final int VIEW_TYPE_ITEM = 0;
	private final int VIEW_TYPE_LOADING = 1;
	private OnLoadMoreListener onLoadMoreListener;
	private boolean isLoading;
	private Context context;
	private List<Visit> visits;
	private int visibleThreshold = 5;
	private int lastVisibleItem, totalItemCount;
	private VisitDataService visitDataService;
	private ObsDataService observationDataService;

	public VisitsAdapter(RecyclerView recyclerView, List<Visit> visits, Context context) {
		this.visits = visits;
		this.context = context;
		this.visitDataService = new VisitDataService();
		this.observationDataService = new ObsDataService();

		final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				totalItemCount = linearLayoutManager.getItemCount();
				lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
				if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
					if (onLoadMoreListener != null) {
						onLoadMoreListener.onLoadMore();
					}
					isLoading = true;
				}
			}
		});
	}

	public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
		this.onLoadMoreListener = mOnLoadMoreListener;
	}

	@Override
	public int getItemViewType(int position) {
		return visits.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == VIEW_TYPE_ITEM) {
			View view = LayoutInflater.from(context).inflate(R.layout.previous_visits_row, parent, false);
			return new VisitViewHolder(view);
		} else if (viewType == VIEW_TYPE_LOADING) {
			View view = LayoutInflater.from(context).inflate(R.layout.previous_visits_loading, parent, false);
			return new LoadingViewHolder(view);
		}
		return null;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof VisitViewHolder) {
			Visit visit = visits.get(position);
			VisitViewHolder viewHolder = (VisitViewHolder)holder;

			//observationViews.setHint("Test");
			for (Encounter encounter : visit.getEncounters()) {
				switch (encounter.getEncounterType().getDisplay()) {
					case ApplicationConstants.EncounterTypeEntitys.VISIT_NOTE:
						observationDataService.getByEncounter(encounter, true, new PagingInfo(0, 20),
								new DataService.GetMultipleCallback<Observation>() {
									@Override
									public void onCompleted(List<Observation> observations, int length) {
										for (Observation observation : observations) {
											if (observation.getDiagnosisNote() != null && !observation.getDiagnosisNote()
													.equals(ApplicationConstants.EMPTY_STRING)) {
												//patientDashboardView.updateActiveVisitObservationsCard(observation);
												DynamicObsDataViews dynamicObsDataViews = new DynamicObsDataViews(context);
												dynamicObsDataViews.setText(observation.getDiagnosisNote());
												//dynamicObsDataViews.observationIcon.setOnClickListener(switchToEditMode);
												//dynamicObsDataViews.observationTextView.setOnClickListener
												//(switchToEditMode);
												viewHolder.observationsContainer.addView(dynamicObsDataViews.getLayoutView
														());
											}
										}
									}

									@Override
									public void onError(Throwable t) {

									}
								});
						break;
				}
			}

		} else if (holder instanceof LoadingViewHolder) {
			LoadingViewHolder loadingViewHolder = (LoadingViewHolder)holder;
			loadingViewHolder.progressBar.setIndeterminate(true);
		}
	}

	@Override
	public int getItemCount() {
		return visits == null ? 0 : visits.size();
	}

	public void setLoaded() {
		isLoading = false;
	}

	private class LoadingViewHolder extends RecyclerView.ViewHolder {
		public ProgressBar progressBar;

		public LoadingViewHolder(View view) {
			super(view);
			progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);
		}
	}

	private class VisitViewHolder extends RecyclerView.ViewHolder {
		public TextView visitDetails;
		public LinearLayout observationsContainer;

		public VisitViewHolder(View view) {
			super(view);
			visitDetails = (TextView)view.findViewById(R.id.visitDetails);
			observationsContainer = (LinearLayout)view.findViewById(R.id.observationsContainer);
		}
	}

}