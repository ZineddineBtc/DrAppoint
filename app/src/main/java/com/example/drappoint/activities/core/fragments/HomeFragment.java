package com.example.drappoint.activities.core.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.drappoint.R;
import com.example.drappoint.activities.entry.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {

    View fragmentView;
    TextView tv;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        tv = fragmentView.findViewById(R.id.text_home);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(fragmentView.getContext(), LoginActivity.class));
            }
        });
        return fragmentView;
    }
}