package com.example.drappoint.activities.core.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drappoint.R;
import com.example.drappoint.adapter.AppointmentsAdapter;
import com.example.drappoint.daos.AppointmentHistoryDAO;
import com.example.drappoint.models.Appointment;

import java.util.ArrayList;

public class AppointmentsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_appointments, container, false);
        ArrayList<Appointment> appointments = new AppointmentHistoryDAO(fragmentView.getContext())
                .getAllAppointments();
        AppointmentsAdapter adapter = new AppointmentsAdapter(fragmentView.getContext(), appointments);
        RecyclerView appointmentsRV = fragmentView.findViewById(R.id.appointmentsRV);
        appointmentsRV.setLayoutManager(new LinearLayoutManager(
                fragmentView.getContext(),
                LinearLayoutManager.VERTICAL, false));
        appointmentsRV.setAdapter(adapter);
        return fragmentView;
    }
}