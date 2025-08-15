package com.example.support_management_system_mobile.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.auth.JWTUtils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditProfileFragment extends Fragment {
    private EditProfileViewModel viewModel;
    private EditText firstNameEdit, surnameEdit, passwordEdit;
    private Button saveChangesButton;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
        initViews(view);

        if (savedInstanceState == null) {
            viewModel.start(
                    JWTUtils.getName(requireContext()),
                    JWTUtils.getSurname(requireContext())
            );
        }

        setupObservers();
        addTextWatchers();
        setupClickListeners();
    }

    private void initViews(View view) {
        firstNameEdit = view.findViewById(R.id.editName);
        surnameEdit = view.findViewById(R.id.editSurname);
        passwordEdit = view.findViewById(R.id.editPassword);
        saveChangesButton = view.findViewById(R.id.saveChangesButton);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupObservers() {
        viewModel.firstName.observe(getViewLifecycleOwner(), name -> {
            if (!name.equals(firstNameEdit.getText().toString())) {
                firstNameEdit.setText(name);
            }
        });

        viewModel.surname.observe(getViewLifecycleOwner(), surname -> {
            if (!surname.equals(surnameEdit.getText().toString())) {
                surnameEdit.setText(surname);
            }
        });

        viewModel.getFormState().observe(getViewLifecycleOwner(), formState -> {
            if (formState == null) return;

            saveChangesButton.setEnabled(formState.isDataValid());

            if (formState.getFirstNameError() != null) {
                firstNameEdit.setError(getString(formState.getFirstNameError()));
            } else {
                firstNameEdit.setError(null);
            }

            if (formState.getSurnameError() != null) {
                surnameEdit.setError(getString(formState.getSurnameError()));
            } else {
                surnameEdit.setError(null);
            }

            if (formState.getPasswordError() != null) {
                passwordEdit.setError(getString(formState.getPasswordError()));
            } else {
                passwordEdit.setError(null);
            }
        });

        viewModel.getUpdateResult().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof EditProfileResult.Loading) {
                showLoading(true);
            } else if (result instanceof EditProfileResult.Success) {
                showLoading(false);
                Toast.makeText(requireContext(), ((EditProfileResult.Success) result).getMessageRes(), Toast.LENGTH_SHORT).show();
                navigateToProfileFragment();
            } else if (result instanceof EditProfileResult.Error) {
                showLoading(false);
                Toast.makeText(requireContext(), ((EditProfileResult.Error) result).getMessageRes(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTextWatchers() {
        firstNameEdit.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.firstName.setValue(firstNameEdit.getText().toString());
            }
        });

        surnameEdit.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.surname.setValue(surnameEdit.getText().toString());
            }
        });

        passwordEdit.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.password.setValue(passwordEdit.getText().toString());
            }
        });
    }

    private void setupClickListeners() {
        saveChangesButton.setOnClickListener(v -> viewModel.saveChanges());
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        saveChangesButton.setEnabled(!isLoading);
    }

    private void navigateToProfileFragment() {
        if (isAdded()) {
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}
