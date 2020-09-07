package com.example.drappoint.activities.core;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drappoint.R;
import com.example.drappoint.StaticClass;
import com.example.drappoint.adapter.SetDate;
import com.example.drappoint.adapter.SetTime;
import com.example.drappoint.daos.AppointmentHistoryDAO;
import com.example.drappoint.models.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DoctorActivity extends AppCompatActivity {

    TextView nameTV, specialtyTV, emailTV, phoneTV, addressTV, maxTV,
                sunday, monday, tuesday, wednesday, thursday, friday, saturday,
            dateTV, reservationsTV, confirmedTV;
    Button confirmButton;
    Doctor doctor;
    LinearLayout shadeLL, pickLL;
    SharedPreferences sharedPreferences;
    FirebaseFirestore database;
    HashMap<String, Long> dateReservations;
    String doctorId = "";
    boolean canConfirm = false;
    long reservations = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);
        sharedPreferences = getSharedPreferences(StaticClass.SHARED_PREFERENCES, MODE_PRIVATE);
        database = FirebaseFirestore.getInstance();
        doctorId = getIntent().getStringExtra(StaticClass.DOCTOR_ID);
        getDoctor();
        setActionBarTitle(doctor.getName());
        findViewsByIds();
        setData();
        new SetDate(dateTV, this);
    }
    private void getDoctor(){
        for(Doctor d: StaticClass.doctors){
            if(d.getId().equals(doctorId)){
                doctor = d;
                break;
            }
        }
        getDateReservations();
    }
    private void getDateReservations(){
        DocumentReference documentReference =
                database.collection("doctors-date")
                        .document(doctorId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        dateReservations = (HashMap<String, Long>)
                                document.get("reservations");
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "get failed with " + task.getException(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void findViewsByIds(){
        nameTV = findViewById(R.id.nameTV);
        specialtyTV = findViewById(R.id.specialtyTV);
        emailTV = findViewById(R.id.emailTV);
        phoneTV = findViewById(R.id.phoneTV);
        addressTV = findViewById(R.id.addressTV);
        maxTV = findViewById(R.id.maxTV);
        sunday = findViewById(R.id.sunday);
        monday = findViewById(R.id.monday);
        tuesday = findViewById(R.id.tuesday);
        wednesday = findViewById(R.id.wednesday);
        thursday = findViewById(R.id.thursday);
        friday = findViewById(R.id.friday);
        saturday = findViewById(R.id.saturday);
        shadeLL = findViewById(R.id.shadeLL);
        pickLL = findViewById(R.id.pickLL);
        pickLL.setTranslationY(100);
        dateTV = findViewById(R.id.dateTV);
        dateTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkReservations();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
        reservationsTV = findViewById(R.id.reservationsTV);
        confirmButton = findViewById(R.id.confirmButton);
        confirmedTV = findViewById(R.id.confirmedTV);
    }
    @SuppressLint("SetTextI18n")
    private void setData(){
        nameTV.setText(doctor.getName());
        specialtyTV.setText(doctor.getSpecialty());
        emailTV.setText(doctor.getId());
        phoneTV.setText(doctor.getPhone());
        addressTV.setText(doctor.getAddress() +", "+ doctor.getCity());
        maxTV.setText("Max: "+doctor.getMax());
        sunday.setText(doctor.getSchedule().get("sunday"));
        monday.setText(doctor.getSchedule().get("monday"));
        tuesday.setText(doctor.getSchedule().get("tuesday"));
        wednesday.setText(doctor.getSchedule().get("wednesday"));
        thursday.setText(doctor.getSchedule().get("thursday"));
        friday.setText(doctor.getSchedule().get("friday"));
        saturday.setText(doctor.getSchedule().get("saturday"));
    }
    public void makeAppointment(View view){
        shadeLL.setVisibility(View.VISIBLE);
        pickLL.animate()
                .setDuration(200)
                .translationYBy(-100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        pickLL.setVisibility(View.VISIBLE);
                    }
                });
    }
    public void dialPhone(View view){
        String phoneNumber = phoneTV.getText().toString()
                .replaceAll("-", "");
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }
    public void openMap(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.setData(
                Uri.parse("https://www.google.com/maps/search/" +
                        addressTV.getText().toString()));
        startActivity(browserIntent);
    }
    public void checkReservations(){
        try{
            reservations = dateReservations.get(dateTV.getText().toString());
            if(reservations < doctor.getMax()){
                canConfirm = true;
                confirmButton.setBackground(getDrawable(R.drawable.special_background_rounded_border));
            }else{
                canConfirm = false;
                confirmButton.setBackground(getDrawable(R.drawable.grey_background_rounded_border));
            }

        }catch (NullPointerException e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        reservationsTV.setText(String.valueOf(reservations));
    }
    public void confirmReservation(View view){
        if(canConfirm) {
            DocumentReference patientReference = database.collection("users")
                    .document(sharedPreferences.getString(StaticClass.EMAIL, ""));
            DocumentReference dateReference =
                    database.collection("doctors-date")
                            .document(doctorId);
            if (dateReservations.containsKey(dateTV.getText().toString())){
                dateReference.update(dateTV.getText().toString(),
                        FieldValue.arrayUnion(patientReference))
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "Error updating patient list",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }else {
                ArrayList<DocumentReference> patientList = new ArrayList<>();
                patientList.add(patientReference);
                Map<String, ArrayList> referenceMap = new HashMap<>();
                referenceMap.put(dateTV.getText().toString(), patientList);
                dateReference.set(referenceMap)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "Error writing patient list",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            onBackPressed();
            displayConfirmed();
            insertAppointment();
        }
    }
    private void insertAppointment(){
        new AppointmentHistoryDAO(this)
                .insertAppointment(doctor, dateTV.getText().toString());
    }
    private void displayConfirmed(){
        confirmedTV.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                confirmedTV.setVisibility(View.GONE);
            }
        }, 1500);
    }
    public void setActionBarTitle(String title){
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        Objects.requireNonNull(getSupportActionBar()).setTitle(
                Html.fromHtml("<font color=\"#ffffff\"> "+title+" </font>")
        );
    }
    @Override
    public void onBackPressed(){
        if(shadeLL.getVisibility()==View.VISIBLE){
            shadeLL.setVisibility(View.GONE);
            pickLL.animate()
                    .setDuration(100)
                    .translationYBy(100)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            pickLL.setVisibility(View.GONE);
                        }
                    });
        }else{
            startActivity(new Intent(getApplicationContext(), DoctorActivity.class));
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
