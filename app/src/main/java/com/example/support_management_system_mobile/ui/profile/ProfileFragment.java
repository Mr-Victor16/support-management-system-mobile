package com.example.support_management_system_mobile.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.support_management_system_mobile.databinding.FragmentProfileBinding;
import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.ui.MainActivity;
import com.example.support_management_system_mobile.ui.management.ManagementPanelFragment;
import com.example.support_management_system_mobile.ui.profile.edit.EditProfileFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        setupButtonListeners();

        observeViewModel();
    }

    private void setupButtonListeners() {
        binding.logoutButton.setOnClickListener(v -> viewModel.onLogoutClicked());
        binding.editProfileButton.setOnClickListener(v -> viewModel.onEditProfileClicked());
        binding.managementPanelButton.setOnClickListener(v -> viewModel.onManagementPanelClicked());
    }

    private void observeViewModel() {
        viewModel.getScreenState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof ProfileUIState.Loading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.contentGroup.setVisibility(View.GONE);
            } else if (state instanceof ProfileUIState.Success successState) {
                binding.progressBar.setVisibility(View.GONE);
                binding.contentGroup.setVisibility(View.VISIBLE);

                binding.usernameText.setText(successState.username);
                binding.fullNameText.setText(successState.fullName);
                binding.emailText.setText(successState.email);
                binding.roleText.setText(successState.roleResId);

                binding.managementPanelButton.setVisibility(successState.isManagementPanelVisible ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getNavigateToLogin().observe(getViewLifecycleOwner(), event -> {
            Boolean shouldNavigate = event.getContentIfNotHandled();

            if (shouldNavigate != null && shouldNavigate) {
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        viewModel.getNavigateToEditProfile().observe(getViewLifecycleOwner(), event -> {
            Boolean shouldNavigate = event.getContentIfNotHandled();

            if (shouldNavigate != null && shouldNavigate) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainContainer, new EditProfileFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        viewModel.getNavigateToManagementPanel().observe(getViewLifecycleOwner(), event -> {
            Boolean shouldNavigate = event.getContentIfNotHandled();


            if (shouldNavigate != null && shouldNavigate) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainContainer, new ManagementPanelFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.refreshUserData();
    }
}
