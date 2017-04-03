package org.openmrs.mobile.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.sampledata.Appointment;

import java.util.List;


public class ListRecentAppointments extends RecyclerView.Adapter<ListRecentAppointments.RecentAppointmentViewHolder> {

    public static class RecentAppointmentViewHolder extends RecyclerView.ViewHolder {


        public TextView appointment_date;


        public RecentAppointmentViewHolder(View itemView) {
            super(itemView);
            appointment_date = (TextView) itemView.findViewById(R.id.appointment_date);
        }
    }

    private List<Appointment> appointmentList;
    private Activity activity;

    public ListRecentAppointments(Activity activity, List<Appointment> appointmentList) {
        this.activity = activity;
        this.appointmentList = appointmentList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RecentAppointmentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_appointments, viewGroup, false);
        return new RecentAppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecentAppointmentViewHolder holder, final int position) {
        holder.appointment_date.setText(appointmentList.get(position).date);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Load data for this appointment
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }
}
