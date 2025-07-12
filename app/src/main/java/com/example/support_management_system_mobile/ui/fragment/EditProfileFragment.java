package com.example.support_management_system_mobile.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.auth.APIClient;
import com.example.support_management_system_mobile.auth.JWTUtils;
import com.example.support_management_system_mobile.payload.request.UpdateProfileRequest;
import com.example.support_management_system_mobile.validators.UserValidator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {
    private EditText firstNameEdit, surnameEdit, passwordEdit;
    private Button saveChangesButton;

    private String originalFirstName, originalSurname;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        initViews(view);
        setupInitialValues();
        addTextWatchers();

        saveChangesButton.setOnClickListener(v -> updateProfileDetails());

        return view;
    }

    private void initViews(View view) {
        firstNameEdit = view.findViewById(R.id.editName);
        surnameEdit = view.findViewById(R.id.editSurname);
        passwordEdit = view.findViewById(R.id.editPassword);
        saveChangesButton = view.findViewById(R.id.saveChangesButton);
    }

    private void setupInitialValues() {
        originalFirstName = JWTUtils.getName(requireContext());
        originalSurname = JWTUtils.getSurname(requireContext());

        firstNameEdit.setText(originalFirstName);
        surnameEdit.setText(originalSurname);

        saveChangesButton.setEnabled(false);
    }

    private void addTextWatchers() {
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateForm();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        firstNameEdit.addTextChangedListener(watcher);
        surnameEdit.addTextChangedListener(watcher);
        passwordEdit.addTextChangedListener(watcher);
    }

    private void validateForm() {
        String firstName = firstNameEdit.getText().toString().trim();
        String surname = surnameEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();

        boolean isFirstNameChanged = !firstName.equals(originalFirstName);
        boolean isSurnameChanged = !surname.equals(originalSurname);
        boolean isPasswordChanged = !password.isEmpty();

        boolean isFirstNameValid = !isFirstNameChanged || UserValidator.isNameValid(firstName);
        boolean isSurnameValid = !isSurnameChanged || UserValidator.isSurnameValid(surname);
        boolean isPasswordValid = !isPasswordChanged || UserValidator.isPasswordValid(password);

        boolean hasChanges = isFirstNameChanged || isSurnameChanged || isPasswordChanged;
        boolean allValid = (!isFirstNameChanged || isFirstNameValid)
                && (!isSurnameChanged || isSurnameValid)
                && (!isPasswordChanged || isPasswordValid);

        saveChangesButton.setEnabled(hasChanges && allValid);
    }

    private void updateProfileDetails() {
        String firstName = firstNameEdit.getText().toString().trim();
        String surname = surnameEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();

        String updatedFirstName = shouldUpdate(firstName, originalFirstName, UserValidator::isNameValid) ? firstName : "";
        String updatedSurname = shouldUpdate(surname, originalSurname, UserValidator::isSurnameValid) ? surname : "";
        String updatedPassword = password.isEmpty() || !UserValidator.isPasswordValid(password) ? "" : password;

        UpdateProfileRequest request = new UpdateProfileRequest(updatedFirstName, updatedSurname, updatedPassword);
        String authToken = "Bearer " + JWTUtils.getToken(requireContext());

        APIClient.getAPIService(requireContext()).updateProfile(authToken, request).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    if (!updatedFirstName.isEmpty()) JWTUtils.setName(requireContext(), updatedFirstName);
                    if (!updatedSurname.isEmpty()) JWTUtils.setSurname(requireContext(), updatedSurname);

                    Toast.makeText(requireActivity(), R.string.profile_updated_successfully, Toast.LENGTH_SHORT).show();

                    navigateToProfileFragment();
                } else {
                    Toast.makeText(requireContext(), R.string.profile_update_failed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Toast.makeText(requireContext(), R.string.server_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean shouldUpdate(String newValue, String originalValue, Validator validator) {
        return !newValue.equals(originalValue) && validator.isValid(newValue);
    }

    @FunctionalInterface
    interface Validator {
        boolean isValid(String input);
    }

    private void navigateToProfileFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}