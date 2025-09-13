package com.example.support_management_system_mobile.ui.welcome;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.ui.knowledge.KnowledgeFragment;
import com.example.support_management_system_mobile.ui.software.SupportedSoftwareFragment;
import com.example.support_management_system_mobile.ui.login.LoginActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WelcomeFragment extends Fragment {
    private WelcomeViewModel viewModel;
    private TextView welcomeHeader;
    private Button loginButton;
    private Button softwareButton;
    private Button knowledgeButton;
    private ProgressBar progressBar;
    private Group contentGroup;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(WelcomeViewModel.class);

        setupViews(view);
        setupClickListeners();
        setupObservers();

        if (savedInstanceState == null) {
            viewModel.loadData();
        }
    }

    private void setupViews(View view) {
        welcomeHeader = view.findViewById(R.id.welcomeHeader);
        loginButton = view.findViewById(R.id.toLoginButton);
        softwareButton = view.findViewById(R.id.toSoftwareButton);
        knowledgeButton = view.findViewById(R.id.toKnowledgeButton);
        progressBar = view.findViewById(R.id.progressBar);
        contentGroup = view.findViewById(R.id.contentGroup);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> viewModel.onLoginClicked());
        softwareButton.setOnClickListener(v -> viewModel.onSoftwareClicked());
        knowledgeButton.setOnClickListener(v -> viewModel.onKnowledgeClicked());
    }

    private void setupObservers() {
        viewModel.uiState.observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            if (state instanceof WelcomeUIState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
                contentGroup.setVisibility(View.GONE);
            } else if (state instanceof WelcomeUIState.Success successState) {
                progressBar.setVisibility(View.GONE);
                contentGroup.setVisibility(View.VISIBLE);

                welcomeHeader.setText(successState.getWelcomeMessage());
                loginButton.setVisibility(successState.isLoginButtonVisible() ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.navigation.observe(getViewLifecycleOwner(), event -> {
            NavigationTarget target = event.getContentIfNotHandled();
            if (target != null) {
                switch (target) {
                    case LOGIN:
                        openLoginActivity();
                        break;
                    case SUPPORTED_SOFTWARE:
                        openSupportedSoftwareFragment();
                        break;
                    case KNOWLEDGE:
                        openKnowledgeFragment();
                        break;
                }
            }
        });
    }

    private void openSupportedSoftwareFragment() {
        navigateToFragment(new SupportedSoftwareFragment());
    }

    private void openKnowledgeFragment() {
        navigateToFragment(new KnowledgeFragment());
    }

    private void navigateToFragment(Fragment fragment) {
        if (!isAdded()) return;
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openLoginActivity() {
        if (!isAdded()) return;
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
    }
}