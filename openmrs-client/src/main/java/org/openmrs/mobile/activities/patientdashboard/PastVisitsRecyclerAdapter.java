package org.openmrs.mobile.activities.patientdashboard;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.auditdata.AuditDataActivity;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.ObsDataService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.List;

public class PastVisitsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private final int VIEW_TYPE_ITEM = 0;
	private final int VIEW_TYPE_LOADING = 1;
	private final Patient patient;
	private OnLoadMoreListener onLoadMoreListener;
	private boolean isLoading;
	private Context context;
	private List<Visit> visits;
	private int visibleThreshold = 5;
	private int lastVisibleItem, totalItemCount;
	private ObsDataService observationDataService;
	private CustomDialogBundle createEditVisitNoteDialog;
	private int startIndex = 0;
	private int limit = 5;
	LayoutInflater layoutInflater;

	View.OnClickListener switchToEditMode = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			((PatientDashboardActivity)context)
					.createAndShowDialog(createEditVisitNoteDialog, ApplicationConstants.DialogTAG.VISIT_NOTE_TAG);
		}
	};

	public PastVisitsRecyclerAdapter(RecyclerView recyclerView, List<Visit> visits, Context context, Patient patient) {
		this.visits = visits;
		this.context = context;
		this.patient = patient;
		createEditVisitNoteDialog = new CustomDialogBundle();
		createEditVisitNoteDialog.setTitleViewMessage(context.getString(R.string.visit_note));
		createEditVisitNoteDialog.setRightButtonText(context.getString(R.string.label_save));
		observationDataService = new ObsDataService();
		layoutInflater = LayoutInflater.from(context);
		LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
			View view = LayoutInflater.from(context).inflate(R.layout.visit_observations_container, parent, false);
			return new VisitViewHolder(view);
		} else if (viewType == VIEW_TYPE_LOADING) {
			View view = LayoutInflater.from(context).inflate(R.layout.past_visits_loading, parent, false);
			return new LoadingViewHolder(view);
		}
		return null;
	}

	public void expandCollapse(View view) {
		if (view.getVisibility() == View.GONE) {
			view.setVisibility(View.VISIBLE);
			//int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			//ConsoleLogger.dump(heightMeasureSpec);
			/*
			final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			view.measure(widthSpec, heightSpec);
			ValueAnimator mAnimator = slideAnimator(view, 0, view.getMeasuredHeight());
			mAnimator.start();*/
		} else {
			view.setVisibility(View.GONE);
			/*int finalHeight = view.getHeight();
			ValueAnimator mAnimator = slideAnimator(view, finalHeight, 0);
			mAnimator.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {

				}

				@Override
				public void onAnimationEnd(Animator animator) {
					view.setVisibility(View.GONE);
				}

				@Override
				public void onAnimationCancel(Animator animation) {

				}

				@Override
				public void onAnimationRepeat(Animator animation) {

				}

			});
			mAnimator.start();*/
		}

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
		if (holder instanceof VisitViewHolder) {

			Visit visit = visits.get(position);
			//setHasVisitNote(false);

			VisitViewHolder viewHolder = (VisitViewHolder)holder;
			View singleVisitView = layoutInflater.inflate(R.layout.patient_visit_card, null);
			LinearLayout observationsContainer = (LinearLayout)singleVisitView.findViewById(R.id.observationsContainer);
			TextView visitTitle = (TextView)singleVisitView.findViewById(R.id.visitTitle);

			if (!StringUtils.notNull(visit.getStopDatetime())) {
				visitTitle.setText(context.getString(R.string.active_visit_label) + ": " + DateUtils
						.convertTime1(visit.getStartDatetime(), DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT));
			} else {
				visitTitle.setText("Visit: " + DateUtils
						.convertTime1(visit.getStopDatetime(), DateUtils
								.PATIENT_DASHBOARD_VISIT_DATE_FORMAT) + " - " + DateUtils
						.convertTime1(visit.getStartDatetime(), DateUtils
								.PATIENT_DASHBOARD_VISIT_DATE_FORMAT));
			}

			visitTitle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					expandCollapse(observationsContainer);
				}
			});

			ImageView loadAuditForm =
					(ImageView)singleVisitView.findViewById(R.id.loadAuditForm);

			loadAuditForm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					Intent intent = new Intent(context, AuditDataActivity
							.class);

					intent.putExtra(ApplicationConstants.BundleKeys
							.VISIT_UUID_BUNDLE, visit.getUuid());

					intent.putExtra(ApplicationConstants.BundleKeys
							.PATIENT_UUID_BUNDLE, patient.getUuid());

					context.startActivity(intent);
				}
			});
			if (visit.getEncounters().size() == 0) {
				addVisitNoteField(observationsContainer, visit.getUuid(), null);
			} else {
				for (Encounter encounter : visit.getEncounters()) {
					switch (encounter.getEncounterType().getDisplay()) {
						case ApplicationConstants.EncounterTypeEntitys.VISIT_NOTE:
							QueryOptions options = new QueryOptions();
							PagingInfo pagininfo =
									new PagingInfo(startIndex, limit);
							observationDataService.getByEncounter(encounter, options, pagininfo,
									new DataService.GetCallback<List<Observation>>() {
										@Override
										public void onCompleted(List<Observation> observations) {
											if (observations.size() == 0) {
												addVisitNoteField(observationsContainer, visit.getUuid(), null);
											} else {
												for (int i = 0; i < observations.size(); i++) {
													Observation observation = observations.get(i);
													if (observation.getDiagnosisNote() != null && !observation
															.getDiagnosisNote()
															.equals(ApplicationConstants.EMPTY_STRING)) {
														addVisitNoteField(observationsContainer, visit.getUuid(),
																observation);
													}

													if ((i == (observations.size() - 1)) && (
															observationsContainer.getChildCount
																	() == 0)) {
														addVisitNoteField(observationsContainer, visit.getUuid(), null);
													}
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
			}

			viewHolder.observationsContainer.addView(singleVisitView);

		} else if (holder instanceof LoadingViewHolder) {
			LoadingViewHolder loadingViewHolder = (LoadingViewHolder)holder;
			loadingViewHolder.progressBar.setIndeterminate(true);
		}
	}

	private void addVisitNoteField(LinearLayout observationsContainer, String visitUUid, @Nullable Observation
			observation) {
		View row = layoutInflater
				.inflate(R.layout.visit_obervation_row, null);
		observationsContainer.addView(row);

		Observation newObservation = new Observation();
		newObservation.setPerson(patient.getPerson());

		ImageView visitNoteIcon = (ImageView)row.findViewById(R.id.icon);
		visitNoteIcon.setOnClickListener(switchToEditMode);

		TextView visitNote = (TextView)row.findViewById(R.id.text);
		visitNote.setHint(context.getResources().getString(R.string
				.add_a_note));
		visitNote.setMovementMethod(new ScrollingMovementMethod());

		if (observation == null) {
			createEditVisitNoteDialog
					.setEditNoteTextViewMessage("");
			createEditVisitNoteDialog.setRightButtonAction(
					CustomFragmentDialog.OnClickAction.CREATE_VISIT_NOTE);
		} else {
			newObservation.setUuid(observation.getUuid());
			newObservation.setConcept(observation.getConcept());
			newObservation.setPerson(patient.getPerson());
			visitNote.setText(observation.getDiagnosisNote());
			createEditVisitNoteDialog.setRightButtonAction(
					CustomFragmentDialog.OnClickAction.SAVE_VISIT_NOTE);
		}

		Bundle dialogBundle = new Bundle();

		dialogBundle
				.putString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE,
						patient.getUuid());

		dialogBundle
				.putString(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE,
						visitUUid);

		dialogBundle.putSerializable(ApplicationConstants.BundleKeys
						.OBSERVATION,
				newObservation);

		createEditVisitNoteDialog.setArguments(dialogBundle);


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