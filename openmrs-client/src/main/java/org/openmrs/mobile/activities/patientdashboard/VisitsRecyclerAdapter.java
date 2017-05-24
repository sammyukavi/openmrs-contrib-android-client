package org.openmrs.mobile.activities.patientdashboard;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.visit.VisitActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.data.impl.ObsDataService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.TimeAgo;

import java.util.ArrayList;
import java.util.List;

public class VisitsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final int VIEW_TYPE_ITEM = 0;
	private final int VIEW_TYPE_LOADING = 1;
	private OnLoadMoreListener onLoadMoreListener;
	private boolean isLoading;
	private Context context;
	private List<Visit> visits;
	private ObsDataService observationDataService;
	private CustomDialogBundle createEditVisitNoteDialog;
	private ImageView showVisitDetails;
	private Intent intent;
	private TimeAgo time;
	private LayoutInflater layoutInflater;
	private TableLayout visitVitalsTableLayout;

	private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);
		}

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			/*totalItemCount = layoutManager.getItemCount();
				lastVisibleItem = layoutManager.findLastVisibleItemPosition();
				if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
					if (onLoadMoreListener != null) {
						onLoadMoreListener.onLoadMore();
					}
					isLoading = true;
				}*/
			if (!isLoading && onLoadMoreListener != null) {
				isLoading = true;
				onLoadMoreListener.onLoadMore();
			}
		}
	};

	public VisitsRecyclerAdapter(RecyclerView recyclerView, List<Visit> visits, Context context) {
		this.visits = visits;
		this.context = context;
		this.createEditVisitNoteDialog = new CustomDialogBundle();
		this.createEditVisitNoteDialog.setTitleViewMessage(context.getString(R.string.visit_note));
		this.createEditVisitNoteDialog.setRightButtonText(context.getString(R.string.label_save));
		this.observationDataService = new ObsDataService();
		this.time = new TimeAgo();
		this.layoutInflater = LayoutInflater.from(context);
		recyclerView.addOnScrollListener(onScrollListener);
	}

	public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
		this.onLoadMoreListener = mOnLoadMoreListener;
	}

	@Override
	public int getItemViewType(int position) {
		return visits.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == VIEW_TYPE_ITEM) {
			View view = LayoutInflater.from(context).inflate(R.layout.container_visits_observations, parent, false);
			return new VisitViewHolder(view);
		} else if (viewType == VIEW_TYPE_LOADING) {
			View view = LayoutInflater.from(context).inflate(R.layout.past_visits_loading, parent, false);
			return new LoadingViewHolder(view);
		}
		return null;
	}

	private ValueAnimator slideAnimator(View view, int start, int end) {
		ValueAnimator animator = ValueAnimator.ofInt(start, end);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				int value = (Integer)valueAnimator.getAnimatedValue();
				ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
				layoutParams.height = value;
				view.setLayoutParams(layoutParams);
			}
		});
		return animator;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

		//View patientContactInfo = layoutInflater.inflate(R.layout.container_patient_contact_info, null);
		//viewHolder.observationsContainer.addView(patientContactInfo);

		if (holder instanceof VisitViewHolder) {
			boolean isActiveVisit = false;
			VisitViewHolder viewHolder = (VisitViewHolder)holder;
			Visit visit = visits.get(position);
			View singleVisitView = layoutInflater.inflate(R.layout.container_single_visit_observation, null);
			TextView visitTitle = (TextView)singleVisitView.findViewById(R.id.visitTitle);
			TextView visitTimeAgo = (TextView)singleVisitView.findViewById(R.id.visitTimeago);

			//Let's set the visit title
			String startDate = DateUtils.convertTime1(visit.getStartDatetime(), DateUtils.DATE_FORMAT);
			String stopDate = visit.getStopDatetime();
			if (!StringUtils.notNull(stopDate)) {
				isActiveVisit = true;
				if (startDate.equalsIgnoreCase(DateUtils.now(DateUtils.DATE_FORMAT))) {
					visitTitle.setText(DateUtils
							.convertTime1(visit.getStartDatetime(), DateUtils.TIME_FORMAT));
				} else {
					visitTitle.setText(DateUtils
							.convertTime1(visit.getStartDatetime(), DateUtils.DATE_FORMAT));
				}

			} else {
				visitTitle.setBackgroundResource(0);
				visitTitle.setTextColor(context.getResources().getColor(R.color.openmrs_color_black));
				visitTitle.setPadding(10, 0, 0, 10);
				stopDate = DateUtils.convertTime1(visit.getStopDatetime(), DateUtils.DATE_FORMAT);
				if (startDate.equalsIgnoreCase(stopDate)) {
					visitTitle.setText(DateUtils.convertTime1(visit.getStartDatetime(), DateUtils.TIME_FORMAT) + " - "
							+ DateUtils.convertTime1(visit.getStopDatetime(), DateUtils.TIME_FORMAT));
				} else {
					visitTitle.setText(DateUtils.convertTime1(visit.getStartDatetime(), DateUtils.DATE_FORMAT) + " - "
							+ DateUtils.convertTime1(visit.getStopDatetime(), DateUtils.DATE_FORMAT));
				}
			}
			visitTimeAgo.setText("Started: " + time.timeAgo(DateUtils.convertTime(visit.getStartDatetime())));

			//Adding the link to the visit details page
			showVisitDetails = (ImageView)singleVisitView.findViewById(R.id.loadVisitDetails);
			showVisitDetails.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					intent = new Intent(context, VisitActivity.class);
					intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, OpenMRS.getInstance()
							.getPatientUuid());
					intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visit.getUuid());
					context.startActivity(intent);
				}
			});

			if (visit.getEncounters().size() == 0) {
				presentVisitNotes(new Encounter(), singleVisitView, isActiveVisit);
			} else {
				for (Encounter encounter : visit.getEncounters()) {
					switch (encounter.getEncounterType().getDisplay()) {
						case ApplicationConstants.EncounterTypeDisplays.VISIT_NOTE:
							presentVisitNotes(encounter, singleVisitView, isActiveVisit);
							break;
						default:
							presentVisitNotes(new Encounter(), singleVisitView, isActiveVisit);
							break;
					}
				}
			}

			viewHolder.observationsContainer.addView(singleVisitView);

		} else if (holder instanceof LoadingViewHolder) {
			LoadingViewHolder loadingViewHolder = (LoadingViewHolder)holder;
			loadingViewHolder.progressBar.setIndeterminate(true);
		}

	}

	private void presentVisitNotes(Encounter encounter, View view, boolean isActiveVisit) {
		TextView visitNote = null;

		if (isActiveVisit) {
			view.findViewById(R.id.editVisitNoteContainer).setVisibility(View.VISIBLE);
			visitNote = (TextInputEditText)view.findViewById(R.id.editVisitNote);
		} else {
			visitNote = (TextView)view.findViewById(R.id.visitNoteText);
			view.findViewById(R.id.visitNoteTextContainer).setVisibility(View.VISIBLE);
		}

		TextView primaryDiagnosis = (TextView)view.findViewById(R.id.primaryDiagnosis);
		TextView secondaryDiagnosis = (TextView)view.findViewById(R.id.secondaryDiagnosis);

		String visitNoteStr = null;
		String primaryObsStr = "";
		String secondaryObsStr = "";
		String otherObsStr = "";
		boolean hasVisitNote = false;

		for (Observation observation : encounter.getObs()) {

			if (observation.getDisplay().startsWith("Text of")) {
				visitNoteStr = observation.getDisplay();
				visitNoteStr = visitNoteStr.substring((visitNoteStr.indexOf(":") + 1), (visitNoteStr.length() - 1));
				hasVisitNote = true;
			} else if (observation.getDisplay().startsWith("Visit Diagnoses: Primary, ")) {
				primaryObsStr += (observation.getDisplay().replaceAll("Visit Diagnoses: Primary, ", "")) + "\n";
			} else if (observation.getDisplay().startsWith("Visit Diagnoses: Secondary,")) {
				secondaryObsStr += observation.getDisplay().replaceAll("Visit Diagnoses: Secondary, ", "") + "\n";
			} else {
				otherObsStr += observation.getDisplay();
				hasVisitNote = true;
			}
		}

		primaryDiagnosis.setText(primaryObsStr.replaceAll("Presumed diagnosis", "").replaceAll(", ", " "));
		secondaryDiagnosis.setText(secondaryObsStr.replaceAll("Presumed diagnosis", "").replaceAll(", ", " "));
		if (visitNoteStr == null) {
			visitNote.setText(otherObsStr);
		} else {
			visitNote.setText(visitNoteStr);
		}

		if (!primaryObsStr.equalsIgnoreCase("")) {
			view.findViewById(R.id.primaryDiagnosisHolder).setVisibility(View.VISIBLE);
		}

		if (!primaryObsStr.equalsIgnoreCase("")) {
			view.findViewById(R.id.secondaryDiagnosisHolder).setVisibility(View.VISIBLE);
		}

		if (!hasVisitNote && !isActiveVisit) {
			view.findViewById(R.id.visitNoteTitle).setVisibility(View.GONE);
			visitNote.setText("No Diagnosis");
		}

	}

	private void presentVitals(Encounter encounter, View singleVisitView) {
		TableLayout visitVitalsTableLayout = (TableLayout)singleVisitView.findViewById(R.id.visitVitalsTable);

		if (encounter.getObs().size() != 0) {
			for (Observation observation : encounter.getObs()) {

				TableRow row = new TableRow(context);
				row.setPadding(0, 20, 0, 10);
				row.setGravity(Gravity.CENTER);

				ArrayList splitValues = StringUtils.splitStrings(observation.getDisplay());

				TextView label = new TextView(context);
				label.setText(splitValues.get(0) + " :");
				label.setTextSize(14);
				label.setGravity(Gravity.RIGHT | Gravity.END);
				label.setTextColor(context.getResources().getColor(R.color.black));
				row.addView(label, 0);

				TextView vitalValue = new TextView(context);
				vitalValue.setText(splitValues.get(1).toString());
				vitalValue.setTextSize(14);
				vitalValue.setTextColor(context.getResources().getColor(R.color.dark_grey));
				row.addView(vitalValue, 1);

				visitVitalsTableLayout.addView(row);
			}
		} else {

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
		protected LinearLayout observationsContainer;

		public VisitViewHolder(View view) {
			super(view);
			observationsContainer = (LinearLayout)view.findViewById(R.id.observationsContainer);
		}
	}
}