package com.example.drappoint.activities.core;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import com.example.drappoint.daos.AppointmentHistoryDAO;
import com.example.drappoint.models.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DoctorActivity extends AppCompatActivity {

    TextView nameTV, specialtyTV, emailTV, phoneTV, addressTV, maxTV, errorTV,
                sunday, monday, tuesday, wednesday, thursday, friday, saturday,
            dateTV, reservationsTV, confirmedTV;
    Button confirmButton;
    LinearLayout shadeLL, pickLL;
    Doctor doctor;
    SharedPreferences sharedPreferences;
    FirebaseFirestore database;
    DocumentReference userReference;
    HashMap<String, Object> wholeDocument;
    HashMap<String, Long> reservedDates;
    ProgressDialog progressDialog;
    String userId;
    boolean canConfirm = false;
    long reservationsNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);
        progressDialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences(StaticClass.SHARED_PREFERENCES, MODE_PRIVATE);
        userId = sharedPreferences.getString(StaticClass.EMAIL, " ");
        doctor = StaticClass.staticDoctor;
        database = FirebaseFirestore.getInstance();
        userReference = database.collection("users")
                .document(userId);
        getWholeDocument();
        setActionBarTitle("Doctor");
    }
    public void findViewsByIds(){
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
        dateTV = findViewById(R.id.dateTV);
        new SetDate(dateTV, DoctorActivity.this);
        reservationsTV = findViewById(R.id.reservationsTV);
        confirmButton = findViewById(R.id.confirmButton);
        confirmedTV = findViewById(R.id.confirmedTV);
        errorTV = findViewById(R.id.errorTV);
        setUIData();
    }
    public void getWholeDocument(){
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        database.collection("doctors-date").document(doctor.getId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        wholeDocument = (HashMap<String, Object>) document.getData();
                        reservedDates = (HashMap<String, Long>)
                                wholeDocument.get("reservations");
                        findViewsByIds();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"get failed with ",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @SuppressLint("SetTextI18n")
    public void setUIData(){
        nameTV.setText(doctor.getName());
        specialtyTV.setText(doctor.getSpecialty());
        emailTV.setText(doctor.getId());
        phoneTV.setText(doctor.getPhone());
        addressTV.setText(doctor.getAddress()+", "+doctor.getCity());
        maxTV.setText("Max: "+doctor.getMax());
        sunday.setText(doctor.getSchedule().get("sunday"));
        monday.setText(doctor.getSchedule().get("monday"));
        tuesday.setText(doctor.getSchedule().get("tuesday"));
        wednesday.setText(doctor.getSchedule().get("wednesday"));
        thursday.setText(doctor.getSchedule().get("thursday"));
        friday.setText(doctor.getSchedule().get("friday"));
        saturday.setText(doctor.getSchedule().get("saturday"));
        progressDialog.dismiss();
        setOnDatePicked();
    }
    public void setOnDatePicked(){
        dateTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(checkIfAlreadyBooked(String.valueOf(s))){
                    canConfirm = false;
                    confirmButton.setBackground(getDrawable(R.drawable.grey_background_rounded_border));
                    errorTV.setVisibility(View.VISIBLE);
                    errorTV.setText(R.string.you_already_booked_this_day);
                }else{
                    if(checkIfThereIsRoom(String.valueOf(s))) {
                        canConfirm = true;
                        confirmButton.setBackground(getDrawable(R.drawable.special_background_rounded_border));
                        errorTV.setVisibility(View.INVISIBLE);
                    }else{
                        canConfirm = false;
                        confirmButton.setBackground(getDrawable(R.drawable.grey_background_rounded_border));
                        errorTV.setVisibility(View.VISIBLE);
                        errorTV.setText(R.string.full);
                    }
                }
                reservationsTV.setText(String.valueOf(reservationsNumber));
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    public boolean checkIfAlreadyBooked(String date){
        ArrayList<DocumentReference> datePatients;
        try {
            datePatients = (ArrayList<DocumentReference>) wholeDocument.get(date);
            if(datePatients.contains(userReference)){
                return true;
            }else{
                return false;
            }
        }catch (NullPointerException e){
            return false;
        }
    }
    public boolean checkIfThereIsRoom(String date){
        try{
            reservationsNumber = reservedDates.get(date);
            return reservationsNumber < doctor.getMax();
        }catch (NullPointerException e){
            reservationsNumber = 0;
            reservedDates.put(date, reservationsNumber);
            return true;
        }
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
    public void showAppointmentPicker(View view) {
        shadeLL.setVisibility(View.VISIBLE);
        pickLL.setVisibility(View.VISIBLE);
    }
    public void insertAppointmentHistory(){
        new AppointmentHistoryDAO(this)
                .insertAppointment(doctor, dateTV.getText().toString());
    }
    public void createFieldThenAdd(){
        ArrayList<DocumentReference> patientList = new ArrayList<>();
        patientList.add(userReference);
        Map<String, ArrayList> dateReferences = new HashMap<>();
        dateReferences.put(dateTV.getText().toString(), patientList);
        database.collection("doctors-date").document(doctor.getId())
                .set(dateReferences, SetOptions.merge())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Error writing patient list",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void addToExistingField(){
        database.collection("doctors-date").document(doctor.getId())
                .update(dateTV.getText().toString(), FieldValue.arrayUnion(userReference))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Error updating patient list",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void incrementCount(){
        reservedDates.put(dateTV.getText().toString(), reservationsNumber+1);
        database.collection("doctors-date").document(doctor.getId())
                .update("reservations", reservedDates)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Error updating patient list",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void confirmAppointment(View view) {
        if(canConfirm){
            if(reservationsNumber==0){
                createFieldThenAdd();
            }else{
                addToExistingField();
            }
            incrementCount();
            onBackPressed();
            insertAppointmentHistory();
            displayConfirmed();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), CoreActivity.class));
                }
            }, 300);
        }
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
            pickLL.setVisibility(View.GONE);
        }else{
            startActivity(new Intent(getApplicationContext(), CoreActivity.class));
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

