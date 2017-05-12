package org.openmrs.mobile.activities.patientdashboard;

import android.content.Context;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.impl.ObsDataService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;

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
	LinearLayout.LayoutParams linearLayoutParams;
	private ObsDataService observationDataService;

	public VisitsAdapter(RecyclerView recyclerView, List<Visit> visits, Context context) {
		this.visits = visits;
		this.context = context;
		observationDataService = new ObsDataService();
		this.linearLayoutParams =
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams
						.WRAP_CONTENT,
						1.0f);
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

	public void expandCollapse(View view) {

		boolean expand = view.getVisibility() == View.GONE;
		Interpolator easeInOutQuart = PathInterpolatorCompat.create(0.77f, 0f, 0.175f, 1f);

		view.measure(
				View.MeasureSpec.makeMeasureSpec(((View)view.getParent()).getWidth(), View.MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
		);

		int height = view.getMeasuredHeight();
		int duration = (int)(height / view.getContext().getResources().getDisplayMetrics().density);

		Animation animation = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if (expand) {
					view.getLayoutParams().height = 10;
					view.setVisibility(View.VISIBLE);
					if (interpolatedTime == 1) {
						view.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
					} else {
						view.getLayoutParams().height = (int)(height * interpolatedTime);
					}
					view.requestLayout();
				} else {
					if (interpolatedTime == 1) {
						view.setVisibility(View.GONE);
					} else {
						view.getLayoutParams().height = height - (int)(height * interpolatedTime);
						view.requestLayout();
					}
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};
		animation.setInterpolator(easeInOutQuart);
		animation.setDuration(duration);
		view.startAnimation(animation);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof VisitViewHolder) {
			Visit visit = visits.get(position);
			VisitViewHolder viewHolder = (VisitViewHolder)holder;
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			View singleVisitView = layoutInflater.inflate(R.layout.previous_visits_card, null);
			LinearLayout observationsContainer = (LinearLayout)singleVisitView.findViewById(R.id.observationsContainer);
			TextView visitTitle = (TextView)singleVisitView.findViewById(R.id.visitTitle);
			visitTitle.setText("Visit: " + DateUtils
					.convertTime1(visit.getStopDatetime(), DateUtils
							.PATIENT_DASHBOARD_DATE_FORMAT) + " - " + DateUtils
					.convertTime1(visit.getStartDatetime(), DateUtils
							.PATIENT_DASHBOARD_DATE_FORMAT));
			visitTitle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					expandCollapse(observationsContainer);
				}
			});
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
												View row = layoutInflater
														.inflate(R.layout.previous_visits_obervation_row, null);
												((TextView)row.findViewById(R.id.text))
														.setText(observation.getDiagnosisNote());
												observationsContainer.addView(row);
											}
										}
									}

									@Override
									public void onError(Throwable t) {
										//patientDashboardView.showSnack("Error fetching observations");
										t.printStackTrace();
									}
								});
						break;
				}
			}
			viewHolder.observationsContainer.addView(singleVisitView);

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
		protected TextView visitDetails;
		protected LinearLayout observationsContainer;

		public VisitViewHolder(View view) {
			super(view);
			observationsContainer = (LinearLayout)view.findViewById(R.id.observationsContainer);
		}
	}
}