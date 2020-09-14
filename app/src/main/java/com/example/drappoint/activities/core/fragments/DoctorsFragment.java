package com.example.drappoint.activities.core.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drappoint.R;
import com.example.drappoint.adapter.DoctorsAdapter;
import com.example.drappoint.models.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DoctorsFragment extends Fragment {

    private View fragmentView;
    private Context context;
    private SearchView searchView;
    private RecyclerView doctorsRV;
    private DoctorsAdapter adapter;
    private ArrayList<Doctor> doctorsList = new ArrayList<>();
    private FirebaseFirestore database;
    private ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_doctors, container, false);
        context = fragmentView.getContext();
        database = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(context);
        downloadData();
        findViewsById();
        return fragmentView;
    }
    private void findViewsById(){
        searchView = fragmentView.findViewById(R.id.doctorsSearchView);
        doctorsRV = fragmentView.findViewById(R.id.doctorsRV);
    }
    private void setRecyclerView(){
        adapter = new DoctorsAdapter(context, doctorsList);
        doctorsRV.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));
        doctorsRV.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });
    }
    private void downloadData(){
        progressDialog.setMessage("Downloading...");
        progressDialog.show();
        database.collection("doctors")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document :
                                    Objects.requireNonNull(task.getResult())) {
                                if(document.exists()) {
                                    Doctor doctor = new Doctor();
                                    doctor.setId(document.getId());
                                    doctor.setName(document.get("name").toString());
                                    doctor.setPhone(document.get("phone").toString());
                                    doctor.setSpecialty(document.get("specialty").toString());
                                    doctor.setAddress(document.get("address").toString());
                                    doctor.setCity(document.get("city").toString());
                                    doctor.setOnVacation(document.getBoolean("vacation"));
                                    doctor.setSchedule((HashMap<String, String>)
                                            document.get("schedule"));
                                    doctor.setMax((long) document.get("max"));
                                    doctorsList.add(doctor);
                                }
                            }
                            setRecyclerView();
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(context,
                                    "Error getting documents." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}