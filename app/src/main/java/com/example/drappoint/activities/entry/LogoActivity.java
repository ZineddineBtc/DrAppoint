package com.example.drappoint.activities.entry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.drappoint.R;
import com.example.drappoint.activities.core.CoreActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LogoActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        Objects.requireNonNull(getSupportActionBar()).hide();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkEntry();
            }
        }, 1000);
    }
    public void checkEntry(){
        if(currentUser == null){
            startActivity(new Intent(getApplicationContext(), SliderActivity.class));
        }else{
            DocumentReference documentReference =
                    database.collection("users")
                            .document(mAuth.getCurrentUser().getEmail());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            startActivity(new Intent(getApplicationContext(), CoreActivity.class));
                        } else {
                            startActivity(new Intent(getApplicationContext(), SetProfileActivity.class));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "get failed with " + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
