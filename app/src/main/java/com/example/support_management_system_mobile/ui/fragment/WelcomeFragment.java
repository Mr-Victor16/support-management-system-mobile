package com.example.support_management_system_mobile.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.ui.activity.LoginActivity;

public class WelcomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        Button loginButton = view.findViewById(R.id.toLoginButton);
        loginButton.setOnClickListener(v -> openLoginActivity());

        Button softwareButton = view.findViewById(R.id.toSoftwareButton);
        softwareButton.setOnClickListener(v -> openSupportedSoftwareFragment());

        Button knowledgeButton = view.findViewById(R.id.toKnowledgeButton);
        knowledgeButton.setOnClickListener(v -> openKnowledge());

        return view;
    }

    private void openSupportedSoftwareFragment() {
        FragmentTransaction transaction = requireActivity()
                .getSupportFragmentManager()
                .beginTransaction();

        transaction.replace(R.id.mainContainer, new SupportedSoftwareFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openLoginActivity() {
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity();
    }

    private void openKnowledge() {
        FragmentTransaction transaction = requireActivity()
                .getSupportFragmentManager()
                .beginTransaction();

        transaction.replace(R.id.mainContainer, new KnowledgeFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}