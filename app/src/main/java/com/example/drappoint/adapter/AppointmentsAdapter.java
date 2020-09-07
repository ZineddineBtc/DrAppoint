package com.example.drappoint.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drappoint.R;
import com.example.drappoint.StaticClass;
import com.example.drappoint.activities.core.DoctorActivity;
import com.example.drappoint.daos.AppointmentHistoryDAO;
import com.example.drappoint.models.Appointment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {

    private List<Appointment> list;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public AppointmentsAdapter(Context context, List<Appointment> list) {
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.appointment_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nameTV.setText(list.get(position).getDoctor().getName());
        holder.specialtyTV.setText(list.get(position).getDoctor().getSpecialty());
        holder.dateTV.setText(list.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTV, specialtyTV ,dateTV, deleteTV;
        ImageView toggleIV;
        boolean isShown;
        AppointmentHistoryDAO dao;
        FirebaseFirestore database;
        SharedPreferences sharedPreferences;

        public ViewHolder(final View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.nameTV);
            specialtyTV = itemView.findViewById(R.id.specialtyTV);
            dateTV = itemView.findViewById(R.id.dateTV);
            dao = new AppointmentHistoryDAO(itemView.getContext());
            database = FirebaseFirestore.getInstance();
            sharedPreferences = itemView.getContext()
                .getSharedPreferences(StaticClass.SHARED_PREFERENCES, MODE_PRIVATE);
            deleteTV = itemView.findViewById(R.id.deleteTV);
            toggleIV = itemView.findViewById(R.id.toggleIV);
            toggleIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleIV.setImageResource(isShown ?
                            R.drawable.ic_show : R.drawable.ic_hide);
                    deleteTV.setVisibility(isShown ?
                            View.GONE : View.VISIBLE);
                    isShown = !isShown;
                }
            });
            deleteTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Delete Appointment")
                            .setMessage("Are you sure you want to delete this appointment?")
                            .setPositiveButton(
                                    Html.fromHtml("<font color=\"#FF0000\"> Delete </font>"), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteStore(
                                                    list.get(getAdapterPosition()).getDoctor().getId());
                                            dao.deleteAppointment(
                                                    list.get(getAdapterPosition()).getDoctor().getId());
                                            Toast.makeText(itemView.getContext(),
                                            list.get(getAdapterPosition()).getDoctor().getName()
                                                            +" removed",
                                                    Toast.LENGTH_SHORT).show();
                                            list.remove(getAdapterPosition());
                                            notifyDataSetChanged();
                                        }
                                    })
                            .setNegativeButton(
                                    Html.fromHtml("<font color=\"#1976D2\"> Cancel </font>"),
                                    null)
                            .show();
                }
            });
            itemView.setOnClickListener(this);
        }
        void deleteStore(String doctorId) {
            DocumentReference patientReference = database.collection("users")
                    .document(sharedPreferences.getString(StaticClass.EMAIL, ""));
            DocumentReference doctorDateReference =
                    database.collection("doctors-date")
                            .document(doctorId);
            doctorDateReference.update(list.get(getAdapterPosition()).getDate(),
                    FieldValue.arrayRemove(patientReference))
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(itemView.getContext(),
                                    "Error removing appointment",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());

            itemView.getContext().startActivity(new Intent(itemView.getContext(),
                    DoctorActivity.class)
            .putExtra(StaticClass.DOCTOR_ID,
                    list.get(getAdapterPosition()).getDoctor().getId()));
        }
    }

    Appointment getItem(int id) {
        return list.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;

    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
