package com.example.support_management_system_mobile.ui.management.user;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.data.models.Role;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserFormFragment extends Fragment {
    private UserManagementViewModel viewModel;

    private ProgressBar loadingProgressBar, submittingProgressBar;
    private ScrollView formContent;
    private TextView errorTextView, formHeader;
    private TextInputLayout usernameInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout nameInputLayout;
    private TextInputLayout surnameInputLayout;
    private TextInputLayout passwordInputLayout;
    private EditText editUsername, editEmail, editName, editSurname, editPassword;
    private AutoCompleteTextView roleAutoComplete;
    private Button saveButton;
    private Long userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(UserManagementViewModel.class);

        userId = getArguments() != null ? getArguments().getLong("userId", -1L) : -1L;
        if (userId == -1L) userId = null;

        initViews(view);

        int headerResId = (userId == null) ? R.string.add_new_user : R.string.edit_user;
        formHeader.setText(headerResId);

        setupListeners();
        observeViewModel();

        viewModel.loadUserForm(userId);
    }

    private void initViews(View view) {
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);
        submittingProgressBar = view.findViewById(R.id.submittingProgressBar);
        formContent = view.findViewById(R.id.formContent);
        errorTextView = view.findViewById(R.id.errorTextView);
        formHeader = view.findViewById(R.id.formHeader);

        usernameInputLayout = view.findViewById(R.id.usernameInputLayout);
        emailInputLayout = view.findViewById(R.id.emailInputLayout);
        nameInputLayout = view.findViewById(R.id.nameInputLayout);
        surnameInputLayout = view.findViewById(R.id.surnameInputLayout);
        passwordInputLayout = view.findViewById(R.id.passwordInputLayout);

        editUsername = view.findViewById(R.id.editUsername);
        editEmail = view.findViewById(R.id.editEmail);
        editName = view.findViewById(R.id.editName);
        editSurname = view.findViewById(R.id.editSurname);
        editPassword = view.findViewById(R.id.editPassword);

        roleAutoComplete = view.findViewById(R.id.roleAutoComplete);
        saveButton = view.findViewById(R.id.saveButton);
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> viewModel.saveUser());

        editUsername.addTextChangedListener(new UserFormFragment.SimpleTextWatcher(s ->
                viewModel.onFieldChanged(UserManagementViewModel.FormField.USERNAME, s)));
        editEmail.addTextChangedListener(new UserFormFragment.SimpleTextWatcher(s ->
                viewModel.onFieldChanged(UserManagementViewModel.FormField.EMAIL, s)));
        editName.addTextChangedListener(new UserFormFragment.SimpleTextWatcher(s ->
                viewModel.onFieldChanged(UserManagementViewModel.FormField.NAME, s)));
        editSurname.addTextChangedListener(new UserFormFragment.SimpleTextWatcher(s ->
                viewModel.onFieldChanged(UserManagementViewModel.FormField.SURNAME, s)));
        editPassword.addTextChangedListener(new UserFormFragment.SimpleTextWatcher(s ->
                viewModel.onFieldChanged(UserManagementViewModel.FormField.PASSWORD, s)));

        roleAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            Role selectedRole = (Role) parent.getItemAtPosition(position);
            viewModel.selectedRole.setValue(selectedRole);
        });
    }

    private void setFormEnabled(boolean isEnabled) {
        usernameInputLayout.setEnabled(isEnabled);
        emailInputLayout.setEnabled(isEnabled);
        nameInputLayout.setEnabled(isEnabled);
        surnameInputLayout.setEnabled(isEnabled);
        passwordInputLayout.setEnabled(isEnabled);
        roleAutoComplete.setEnabled(isEnabled);
        saveButton.setEnabled(isEnabled);
    }

    private void observeViewModel() {
        viewModel.userFormState.observe(getViewLifecycleOwner(), state -> {
            loadingProgressBar.setVisibility(View.GONE);
            formContent.setVisibility(View.GONE);
            errorTextView.setVisibility(View.GONE);
            submittingProgressBar.setVisibility(View.GONE);

            if (state instanceof UserFormUIState.Loading) {
                loadingProgressBar.setVisibility(View.VISIBLE);
            }
            else if (state instanceof UserFormUIState.Error) {
                errorTextView.setVisibility(View.VISIBLE);
                errorTextView.setText(((UserFormUIState.Error) state).message);
            }
            else if (state instanceof UserFormUIState.Submitting) {
                formContent.setVisibility(View.VISIBLE);
                setFormEnabled(false);
                submittingProgressBar.setVisibility(View.VISIBLE);
            }
            else if (state instanceof UserFormUIState.Editing) {
                formContent.setVisibility(View.VISIBLE);
                setFormEnabled(true);
                saveButton.setText(((UserFormUIState.Editing) state).saveButtonTextResId);

                passwordInputLayout.setVisibility(userId == null ? View.VISIBLE : View.GONE);
            }
            else if (state instanceof UserFormUIState.Success) {
                getParentFragmentManager().popBackStack();
            }
        });

        viewModel.usernameError.observe(getViewLifecycleOwner(), errorResId ->
                usernameInputLayout.setError(errorResId != null ? getString(errorResId) : null));

        viewModel.nameError.observe(getViewLifecycleOwner(), errorResId ->
                nameInputLayout.setError(errorResId != null ? getString(errorResId) : null));

        viewModel.surnameError.observe(getViewLifecycleOwner(), errorResId ->
                surnameInputLayout.setError(errorResId != null ? getString(errorResId) : null));

        viewModel.emailError.observe(getViewLifecycleOwner(), errorResId ->
                emailInputLayout.setError(errorResId != null ? getString(errorResId) : null));

        viewModel.passwordError.observe(getViewLifecycleOwner(), errorResId ->
                passwordInputLayout.setError(errorResId != null ? getString(errorResId) : null));

        viewModel.isFormValid.observe(getViewLifecycleOwner(), isValid ->
                saveButton.setEnabled(isValid != null && isValid));

        viewModel.roleList.observe(getViewLifecycleOwner(), roleList -> {
            if (getContext() == null || roleList == null) return;

            ArrayAdapter<Role> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, roleList);
            roleAutoComplete.setAdapter(adapter);
            setDropdownSelection(roleAutoComplete, viewModel.selectedRole.getValue());
        });

        viewModel.username.observe(getViewLifecycleOwner(), text -> {
            if (!Objects.equals(editUsername.getText().toString(), text)) editUsername.setText(text);
        });
        viewModel.name.observe(getViewLifecycleOwner(), text -> {
            if (!Objects.equals(editName.getText().toString(), text)) editName.setText(text);
        });
        viewModel.surname.observe(getViewLifecycleOwner(), text -> {
            if (!Objects.equals(editSurname.getText().toString(), text)) editSurname.setText(text);
        });
        viewModel.email.observe(getViewLifecycleOwner(), text -> {
            if (!Objects.equals(editEmail.getText().toString(), text)) editEmail.setText(text);
        });
        viewModel.password.observe(getViewLifecycleOwner(), text -> {
            if (!Objects.equals(editPassword.getText().toString(), text)) editPassword.setText(text);
        });

        viewModel.selectedRole.observe(getViewLifecycleOwner(), selection -> setDropdownSelection(roleAutoComplete, selection));

        viewModel.toastMessage.observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null && isResumed()) Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    private void setDropdownSelection(AutoCompleteTextView dropdown, Role value) {
        if (value != null) {
            dropdown.setText(value.toString(), false);
        } else {
            dropdown.setText("", false);
        }
    }

    private record SimpleTextWatcher(java.util.function.Consumer<String> onTextChanged) implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {    }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            onTextChanged.accept(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {  }
    }
}