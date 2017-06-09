package org.openmrs.mobile.activities.visit.detail;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.visit.VisitContract;
import org.openmrs.mobile.models.VisitPhoto;

import java.util.List;

public class DiagnosisRecyclerViewAdapter extends RecyclerView.Adapter<DiagnosisRecyclerViewAdapter.DiagnosisViewHolder> {

	private Activity context;
	private VisitContract.VisitDetailsView view;
	private List<VisitPhoto> visitPhotos;

	public DiagnosisRecyclerViewAdapter(Activity context,
			List<VisitPhoto> visitPhotos, VisitContract.VisitDetailsView view) {
		this.context = context;
		this.visitPhotos = visitPhotos;
		this.view = view;
	}

	@Override
	public DiagnosisViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diagnosis_row, parent, false);
		return new DiagnosisViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(DiagnosisViewHolder holder, int position) {

	}

	@Override
	public int getItemCount() {
		return visitPhotos.size();
	}

	class DiagnosisViewHolder extends RecyclerView.ViewHolder {
		private LinearLayout rowLayout;
		private TextView diagnosis;
		private CheckBox primary, confirmed;

		public DiagnosisViewHolder(View itemView) {
			super(itemView);
			rowLayout = (LinearLayout)itemView;
			diagnosis = (TextView)itemView.findViewById(R.id.diagnosisDisplay);
			primary = (CheckBox)itemView.findViewById(R.id.primary);
			confirmed = (CheckBox)itemView.findViewById(R.id.confirmed);
		}
	}
}
