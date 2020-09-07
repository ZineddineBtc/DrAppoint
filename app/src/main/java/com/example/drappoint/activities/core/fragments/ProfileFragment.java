package com.example.drappoint.activities.core.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.drappoint.R;
import com.example.drappoint.StaticClass;
import com.example.drappoint.activities.SettingsActivity;
import com.example.drappoint.activities.TermsActivity;
import com.example.drappoint.activities.entry.LoginActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private View fragmentView;
    private Context context;
    private SharedPreferences sharedPreferences;
    private TextView nameTV, emailTV, phoneTV, addressTV, signOutTV, termsTV, errorTV;
    private EditText nameET, phoneET, addressET;
    private ImageView photoIV, editNameIV, editPhoneIV, editAddressIV;
    private boolean isNameEdit, isPhoneEdit, isAddressEdit;
    private FirebaseFirestore database;
    private Map<String, Object> userReference = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_profile, container, false);
        context = fragmentView.getContext();
        setHasOptionsMenu(true);
        sharedPreferences = context.getSharedPreferences(StaticClass.SHARED_PREFERENCES, MODE_PRIVATE);
        database = FirebaseFirestore.getInstance();
        findViewsByIds();
        initializeData();
        setClickListeners();
        return fragmentView;
    }
    private void findViewsByIds(){
        photoIV = fragmentView.findViewById(R.id.photoIV);
        nameTV = fragmentView.findViewById(R.id.nameTV);
        nameET = fragmentView.findViewById(R.id.nameET);
        emailTV = fragmentView.findViewById(R.id.emailTV);
        phoneTV = fragmentView.findViewById(R.id.phoneTV);
        phoneET = fragmentView.findViewById(R.id.phoneET);
        addressTV = fragmentView.findViewById(R.id.addressTV);
        addressET = fragmentView.findViewById(R.id.addressET);
        editNameIV = fragmentView.findViewById(R.id.editNameIV);
        editPhoneIV = fragmentView.findViewById(R.id.editPhoneIV);
        editAddressIV = fragmentView.findViewById(R.id.editAddressIV);
        signOutTV = fragmentView.findViewById(R.id.signOutTV);
        termsTV = fragmentView.findViewById(R.id.termsTV);
        errorTV = fragmentView.findViewById(R.id.errorTV);
    }
    private void initializeData(){
        if(!sharedPreferences.getString(StaticClass.PHOTO, "").isEmpty()){
            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(
                        context.getContentResolver(),
                        Uri.parse(sharedPreferences.getString(StaticClass.PHOTO, "")));
            } catch (IOException e) {
                Toast.makeText(context, "IO Exception",
                        Toast.LENGTH_LONG).show();
            }
            photoIV.setImageBitmap(imageBitmap);
        }
        nameTV.setText(sharedPreferences.getString(StaticClass.NAME, "no username"));
        nameET.setText(sharedPreferences.getString(StaticClass.NAME, ""));
        emailTV.setText(sharedPreferences.getString(StaticClass.EMAIL, "no email"));
        phoneTV.setText(sharedPreferences.getString(StaticClass.PHONE, "no phone number"));
        phoneET.setText(sharedPreferences.getString(StaticClass.PHONE, ""));
        addressTV.setText(sharedPreferences.getString(StaticClass.ADDRESS, "no address"));
        addressET.setText(sharedPreferences.getString(StaticClass.ADDRESS, ""));
    }
    private void setClickListeners(){
        photoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importImage();
            }
        });
        editNameIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditName();
            }
        });
        editPhoneIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditPhone();
            }
        });
        editAddressIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditAddress();
            }
        });
        signOutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        termsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(fragmentView.getContext(), TermsActivity.class));
            }
        });
    }
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(fragmentView.getContext(), LoginActivity.class));
    }
    private void toggleEditName(){
        nameTV.setVisibility(isNameEdit ? View.VISIBLE : View.GONE);
        nameET.setVisibility(isNameEdit ? View.GONE : View.VISIBLE);
        editNameIV.setImageResource(isNameEdit ?
                R.drawable.ic_edit : R.drawable.ic_check);
        updateName(nameET.getText().toString().trim());
        isNameEdit = !isNameEdit;
    }
    private void updateName(String newName){
        if(isNameEdit){
            if(!newName.equals(sharedPreferences.getString(StaticClass.NAME, ""))
                    && !newName.isEmpty() && !StaticClass.containsDigit(newName)){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(StaticClass.NAME, newName);
                editor.apply();
                initializeData();
                userReference.put("name", newName);
                database.collection("users")
                        .document(emailTV.getText().toString())
                        .update(userReference);
                Snackbar.make(fragmentView.findViewById(R.id.parentLayout),
                        "Name updated", 1000)
                        .setAction("Action", null).show();
            }else{
                displayError();
            }
        }
    }
    private void toggleEditPhone(){
        phoneTV.setVisibility(isPhoneEdit ? View.VISIBLE : View.GONE);
        phoneET.setVisibility(isPhoneEdit ? View.GONE : View.VISIBLE);
        editPhoneIV.setImageResource(isPhoneEdit ?
                R.drawable.ic_edit : R.drawable.ic_check);
        updatePhone(phoneET.getText().toString().trim());
        isPhoneEdit = !isPhoneEdit;
    }
    private void updatePhone(String newPhone){
        if(isPhoneEdit){
            if(!newPhone.equals(sharedPreferences.getString(StaticClass.PHONE, ""))
                    && newPhone.length()>9){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(StaticClass.PHONE, newPhone);
                editor.apply();
                initializeData();
                userReference.put("phone", newPhone);
                database.collection("users")
                        .document(emailTV.getText().toString())
                        .update(userReference);
                Snackbar.make(fragmentView.findViewById(R.id.parentLayout),
                        "Phone updated", 1000)
                        .setAction("Action", null).show();
            }else{
                displayError();
            }

        }
    }
    private void toggleEditAddress(){
        addressTV.setVisibility(isAddressEdit ? View.VISIBLE : View.GONE);
        addressET.setVisibility(isAddressEdit ? View.GONE : View.VISIBLE);
        editAddressIV.setImageResource(isAddressEdit ?
                R.drawable.ic_edit : R.drawable.ic_check);
        updateAddress(addressET.getText().toString().trim());
        isAddressEdit = !isAddressEdit;
    }
    private void updateAddress(String newAddress){
        if(isAddressEdit){
            if(!newAddress.equals(sharedPreferences.getString(StaticClass.ADDRESS, ""))
                    && !newAddress.isEmpty()){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(StaticClass.ADDRESS, newAddress);
                editor.apply();
                initializeData();
                userReference.put("address", newAddress);
                database.collection("users")
                        .document(emailTV.getText().toString())
                        .update(userReference);
                Snackbar.make(fragmentView.findViewById(R.id.parentLayout),
                        "Address updated", 1000)
                        .setAction("Action", null).show();
            }else{
                displayError();
            }

        }
    }
    private void importImage(){
        Intent intent;
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select Images"),
                StaticClass.PICK_SINGLE_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StaticClass.PICK_SINGLE_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri uri = data.getData();
            if(uri != null){
                final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                ContentResolver resolver = context.getContentResolver();
                resolver.takePersistableUriPermission(uri, takeFlags);

                Bitmap imageBitmap = null;
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(
                            context.getContentResolver(), uri);
                } catch (IOException e) {
                    Toast.makeText(context, "IO Exception",
                            Toast.LENGTH_LONG).show();
                }
                photoIV.setImageBitmap(imageBitmap);
                updatePhoto(String.valueOf(uri));
            }
        }
    }
    private void updatePhoto(String uriString){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(StaticClass.PHOTO, String.valueOf(uriString));
        editor.apply();
        initializeData();
    }
    private void displayError(){
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.openSettings){
            startActivity(new Intent(context, SettingsActivity.class));
        }
        return false;
    }
}














