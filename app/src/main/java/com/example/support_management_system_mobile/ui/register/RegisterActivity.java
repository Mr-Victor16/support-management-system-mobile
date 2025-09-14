package com.example.support_management_system_mobile.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.support_management_system_mobile.utils.AuthContext;
import com.example.support_management_system_mobile.ui.MainActivity;
import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.ui.login.LoginActivity;
import com.google.android.material.textfield.TextInputLayout;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterActivity extends AppCompatActivity {
    private RegisterViewModel viewModel;

    private EditText editUsername, editName, editSurname, editEmail, editPassword;
    private TextInputLayout usernameLayout, nameLayout, surnameLayout, emailLayout, passwordLayout;
    private Button registerButton;
    private ProgressBar progressBar;

    @Inject
    AuthContext authContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        if (authContext.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        initViews();
        setupInputListeners();
        setupObservers();

        registerButton.setOnClickListener(v -> viewModel.register());
    }

    private void initViews() {
        editUsername = findViewById(R.id.editUsername);
        editName = findViewById(R.id.editName);
        editSurname = findViewById(R.id.editSurname);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);

        usernameLayout = findViewById(R.id.usernameLayout);
        nameLayout = findViewById(R.id.nameLayout);
        surnameLayout = findViewById(R.id.surnameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);

        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupInputListeners() {
        editUsername.addTextChangedListener((SimpleTextWatcher) (s, start, before, count) -> viewModel.onUsernameChanged(s.toString()));
        editName.addTextChangedListener((SimpleTextWatcher) (s, start, before, count) -> viewModel.onNameChanged(s.toString()));
        editSurname.addTextChangedListener((SimpleTextWatcher) (s, start, before, count) -> viewModel.onSurnameChanged(s.toString()));
        editEmail.addTextChangedListener((SimpleTextWatcher) (s, start, before, count) -> viewModel.onEmailChanged(s.toString()));
        editPassword.addTextChangedListener((SimpleTextWatcher) (s, start, before, count) -> viewModel.onPasswordChanged(s.toString()));
    }

    private void setupObservers() {
        viewModel.getFormState().observe(this, state -> {
            if (state == null) return;
            usernameLayout.setError(state.getUsernameError() != null ? getString(state.getUsernameError()) : null);
            nameLayout.setError(state.getNameError() != null ? getString(state.getNameError()) : null);
            surnameLayout.setError(state.getSurnameError() != null ? getString(state.getSurnameError()) : null);
            emailLayout.setError(state.getEmailError() != null ? getString(state.getEmailError()) : null);
            passwordLayout.setError(state.getPasswordError() != null ? getString(state.getPasswordError()) : null);
        });

        viewModel.getIsLoading().observe(this, isLoading ->
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getIsRegisterButtonEnabled().observe(this, isEnabled ->
                registerButton.setEnabled(isEnabled));

        viewModel.getResult().observe(this, result -> {
            if (result instanceof RegisterUIState.Success) {
                Toast.makeText(this, R.string.successfully_registered, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else if (result instanceof RegisterUIState.Error) {
                Toast.makeText(this, getString(((RegisterUIState.Error) result).getMessageRes()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @FunctionalInterface
    interface SimpleTextWatcher extends TextWatcher {
        @Override
        default void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        default void afterTextChanged(Editable s) {}
    }
}