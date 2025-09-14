package com.example.support_management_system_mobile.ui.profile.edit;

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
import com.example.support_management_system_mobile.utils.AuthContext;
import com.google.android.material.textfield.TextInputLayout;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditProfileFragment extends Fragment {
    private EditProfileViewModel viewModel;

    @Inject
    AuthContext authContext;

    private TextInputLayout firstNameLayout, surnameLayout, passwordLayout;
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
            viewModel.start(authContext.getName(), authContext.getSurname());
        }

        setupObservers();
        addTextWatchers();
        setupClickListeners();
    }

    private void initViews(View view) {
        firstNameLayout = view.findViewById(R.id.editNameLayout);
        surnameLayout = view.findViewById(R.id.editSurnameLayout);
        passwordLayout = view.findViewById(R.id.editPasswordLayout);

        firstNameEdit = view.findViewById(R.id.editName);
        surnameEdit = view.findViewById(R.id.editSurname);
        passwordEdit = view.findViewById(R.id.editPassword);

        saveChangesButton = view.findViewById(R.id.saveChangesButton);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupObservers() {
        viewModel.firstName.observe(getViewLifecycleOwner(), name -> {
            if (firstNameEdit != null && !name.equals(firstNameEdit.getText().toString())) {
                firstNameEdit.setText(name);
            }
        });

        viewModel.surname.observe(getViewLifecycleOwner(), surname -> {
            if (surnameEdit != null && !surname.equals(surnameEdit.getText().toString())) {
                surnameEdit.setText(surname);
            }
        });

        viewModel.getFormState().observe(getViewLifecycleOwner(), formState -> {
            if (formState == null) return;

            saveChangesButton.setEnabled(formState.isDataValid() && formState.hasChanges());

            firstNameLayout.setError(formState.getFirstNameError() != null ? getString(formState.getFirstNameError()) : null);
            surnameLayout.setError(formState.getSurnameError() != null ? getString(formState.getSurnameError()) : null);
            passwordLayout.setError(formState.getPasswordError() != null ? getString(formState.getPasswordError()) : null);
        });

        viewModel.getUpdateResult().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof EditProfileUIState.Loading) {
                showLoading(true);
            } else if (result instanceof EditProfileUIState.Success) {
                showLoading(false);
                Toast.makeText(requireContext(), ((EditProfileUIState.Success) result).getMessageRes(), Toast.LENGTH_SHORT).show();
                navigateToProfileFragment();
            } else if (result instanceof EditProfileUIState.Error) {
                showLoading(false);
                Toast.makeText(requireContext(), ((EditProfileUIState.Error) result).getMessageRes(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTextWatchers() {
        firstNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.firstName.setValue(s.toString());
            }
        });

        surnameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.surname.setValue(s.toString());
            }
        });

        passwordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.password.setValue(s.toString());
            }
        });
    }

    private void setupClickListeners() {
        saveChangesButton.setOnClickListener(v -> viewModel.saveChanges());
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        saveChangesButton.setEnabled(!isLoading);
    }

    private void navigateToProfileFragment() {
        if (isAdded()) {
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }
}
