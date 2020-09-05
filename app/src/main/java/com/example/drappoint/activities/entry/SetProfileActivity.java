package com.example.drappoint.activities.entry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drappoint.R;
import com.example.drappoint.StaticClass;
import com.example.drappoint.activities.TermsActivity;
import com.example.drappoint.activities.core.CoreActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SetProfileActivity extends AppCompatActivity {

    EditText nameET, phoneET, addressET;
    TextView errorTV;
    SharedPreferences sharedPreferences;
    String name, phone, address, email;
    FirebaseFirestore database;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);
        Objects.requireNonNull(getSupportActionBar()).hide();
        sharedPreferences = getSharedPreferences(StaticClass.SHARED_PREFERENCES, MODE_PRIVATE);
        database = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        findViewsByIds();
        checkBuildVersion();
    }
    public void findViewsByIds(){
        nameET = findViewById(R.id.nameET);
        nameET.requestFocus();
        phoneET = findViewById(R.id.phoneET);
        addressET = findViewById(R.id.addressET);
        errorTV = findViewById(R.id.errorTV);
    }
    public void checkBuildVersion(){
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyHavePermission()) {
                requestForSpecificPermission();
            }
        }
    }
    private boolean checkIfAlreadyHavePermission() {
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET},
                101);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // not granted
                moveTaskToBack(true);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    public void writeSharedPreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(StaticClass.NAME, name);
        editor.putString(StaticClass.PHONE, phone);
        editor.putString(StaticClass.ADDRESS, address);
        editor.putString(StaticClass.EMAIL, email);
        editor.apply();
    }
    public void writeOnlineDatabase(){
        Map<String, Object> storeReference = new HashMap<>();
        storeReference.put("name", name);
        storeReference.put("phone", phone);
        storeReference.put("address", address);
        database.collection("users")
                .document(email)
                .set(storeReference)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(getApplicationContext(), CoreActivity.class));
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Error writing user",
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }
    public void finishRegister(View view) {
        name = nameET.getText().toString().trim();
        phone = phoneET.getText().toString().trim();
        address = addressET.getText().toString().trim();
        email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())
                .getEmail();
        if(name.length()<2){
            displayErrorTV(R.string.name_unspecified);
            return;
        }
        if (StaticClass.containsDigit(name)) {
            displayErrorTV(R.string.name_not_number);
            return;
        }
        if (phone.length() < 10) {
            displayErrorTV(R.string.insufficient_phone_number);
            return;
        }
        if (address.isEmpty()) {
            displayErrorTV(R.string.address_unspecified);
            return;
        }
        progressDialog.setMessage("Setting up profile...");
        progressDialog.show();
        writeSharedPreferences();
        writeOnlineDatabase();
    }
    public void toTermsAndConditions(View view) {
        startActivity(new Intent(getApplicationContext(), TermsActivity.class));
    }
    public void displayErrorTV(int resourceID) {
        errorTV.setText(resourceID);
        errorTV.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                errorTV.setVisibility(View.GONE);
            }
        }, 1500);
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
