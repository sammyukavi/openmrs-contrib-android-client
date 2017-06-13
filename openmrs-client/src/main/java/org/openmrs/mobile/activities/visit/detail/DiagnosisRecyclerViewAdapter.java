package org.openmrs.mobile.activities.visit.detail;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.visit.VisitContract;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.HashMap;
import java.util.List;

public class DiagnosisRecyclerViewAdapter extends RecyclerView.Adapter<DiagnosisRecyclerViewAdapter.DiagnosisViewHolder> {

	private Activity context;
	private VisitContract.VisitDetailsView visitDetailsView;
	private List<HashMap<String, Object>> diagnoses;

	public DiagnosisRecyclerViewAdapter(Activity context,
			List<HashMap<String, Object>> diagnoses, VisitContract.VisitDetailsView visitDetailsView) {
		this.context = context;
		this.diagnoses = diagnoses;
		this.visitDetailsView = visitDetailsView;
	}

	@Override
	public DiagnosisViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diagnosis_row, parent, false);
		return new DiagnosisViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(DiagnosisViewHolder holder, int position) {
		HashMap<String, Object> hashMap = diagnoses.get(position);

		String confirmedDiagnosis = (String)hashMap.get("certainty");
		String diagnosisOrder = (String)hashMap.get("order");
		String diagnosis = (String)hashMap.get("diagnosis");

		if (confirmedDiagnosis != null) {
			if (confirmedDiagnosis.equalsIgnoreCase(ApplicationConstants.DiagnosisStrings.PRESUMED)) {
				holder.certainty.setChecked(false);
			} else {
				holder.certainty.setChecked(true);
			}
		}

		if (diagnosisOrder != null) {
			if (diagnosisOrder.equalsIgnoreCase(ApplicationConstants.DiagnosisStrings.SECONDARY_ORDER)) {
				holder.order.setChecked(false);
			} else {
				holder.order.setChecked(true);
			}
		}

		if (diagnosis != null) {
			holder.diagnosis.setText(diagnosis);
		}

		holder.order.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton orderCheckBox, boolean isChecked) {
				if (orderCheckBox.isChecked()) {
					hashMap.replace("order", ApplicationConstants.DiagnosisStrings.PRIMARY_ORDER);
					visitDetailsView.setPrimaryDiagnosis(hashMap);

				} else {
					hashMap.replace("order", ApplicationConstants.DiagnosisStrings.SECONDARY_ORDER);
					visitDetailsView.setSecondaryDiagnosis(hashMap);
				}
			}
		});

		holder.certainty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton certaintyCheckBox, boolean isChecked) {
				if (certaintyCheckBox.isChecked()) {
					hashMap.replace("certainty", ApplicationConstants.DiagnosisStrings.CONFIRMED);
					visitDetailsView.setDiagnosisCertainty(hashMap, diagnosisOrder);
				} else {
					hashMap.replace("certainty", ApplicationConstants.DiagnosisStrings.PRESUMED);
					visitDetailsView.setDiagnosisCertainty(hashMap, diagnosisOrder);
				}
			}
		});

		holder.rowLayout.setLongClickable(true);
		holder.rowLayout.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				visitDetailsView.removeDiagnosis(hashMap, diagnosisOrder);
				return true;
			}
		});
	}

	@Override
	public int getItemCount() {
		return diagnoses.size();
	}

	class DiagnosisViewHolder extends RecyclerView.ViewHolder {
		private CardView rowLayout;
		private TextView diagnosis;
		private CheckBox order, certainty;

		public DiagnosisViewHolder(View itemView) {
			super(itemView);
			rowLayout = (CardView)itemView;
			diagnosis = (TextView)itemView.findViewById(R.id.diagnosisDisplay);
			order = (CheckBox)itemView.findViewById(R.id.order);
			certainty = (CheckBox)itemView.findViewById(R.id.certainty);
		}
	}
}
