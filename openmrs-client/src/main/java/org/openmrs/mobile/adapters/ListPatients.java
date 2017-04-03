package org.openmrs.mobile.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import org.openmrs.mobile.R;
import org.openmrs.mobile.fragments.PatientDetails;
import org.openmrs.mobile.interfaces.PassPatientData;
import org.openmrs.mobile.sampledata.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;


public class ListPatients extends RecyclerView.Adapter<ListPatients.PatientViewHolder> {

    public static class PatientViewHolder extends RecyclerView.ViewHolder {


        public TextView given_name;
        public TextView middle_name;
        public TextView family_name;
        public TextView age;
        public TextView id;
        public ImageView gender;
        public ImageView active_visit;

        public PatientViewHolder(View itemView) {
            super(itemView);
            given_name = (TextView) itemView.findViewById(R.id.given_name);
            middle_name = (TextView) itemView.findViewById(R.id.middle_name);
            family_name = (TextView) itemView.findViewById(R.id.family_name);
            age = (TextView) itemView.findViewById(R.id.age);
            id = (TextView) itemView.findViewById(R.id.patient_id);
            gender = (ImageView) itemView.findViewById(R.id.genderIcon);
            active_visit = (ImageView) itemView.findViewById(R.id.activeVisitIcon);
        }
    }

    private List<Patient> patients;
    private Activity activity;
    private PassPatientData dataPasser;

    public ListPatients(Activity activity, List<Patient> patients) {
        this.activity = activity;
        this.patients = patients;
       // this.dataPasser = (PassPatientData) activity;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PatientViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_patients, viewGroup, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PatientViewHolder holder, final int position) {
        holder.given_name.setText(patients.get(position).given_name);
        holder.middle_name.setText(patients.get(position).middle_name);
        holder.family_name.setText(patients.get(position).family_name);
        holder.age.setText(String.valueOf(patients.get(position).age));
        holder.id.setText(patients.get(position).id);
        holder.gender.setImageResource(String.valueOf(patients.get(position).gender).toLowerCase().equals("m") ? R.drawable.ic_male : R.drawable.ic_female);
        if (patients.get(position).active_visit == 1) {
            holder.active_visit.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Fragment fragment = PatientDetails.class.newInstance();
                    //dataPasser.onPatientDataPass(patients.get(position));
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ApplicationConstants.Tags.PATIENT_DATA, patients.get(position));
                    fragment.setArguments(bundle);
                    ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction().setCustomAnimations(
                            R.anim.slide_in_left, R.anim.slide_out_right,
                            R.anim.pop_enter, R.anim.pop_exit).replace(R.id.fragment_container, fragment).addToBackStack(null).commit();


                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }
}
