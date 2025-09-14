package com.example.support_management_system_mobile.ui.login;

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
import com.example.support_management_system_mobile.ui.register.RegisterActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;

    private EditText editUsername;
    private EditText editPassword;
    private Button loginButton;
    private Button createAccountButton;
    private ProgressBar progressBar;

    @Inject
    AuthContext authContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        if (authContext.isLoggedIn()) {
            startMain();
            return;
        }

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        initViews();
        setupInputListeners();
        setupObservers();

        loginButton.setOnClickListener(v -> viewModel.loginUser());
        createAccountButton.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void initViews() {
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupInputListeners() {
        editUsername.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.onUsernameChanged(s.toString());
            }
        });

        editPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.onPasswordChanged(s.toString());
            }
        });
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            createAccountButton.setEnabled(!isLoading);
        });

        viewModel.getIsLoginButtonEnabled().observe(this, isEnabled -> loginButton.setEnabled(isEnabled));

        viewModel.getResult().observe(this, result -> {
            if (result instanceof LoginUIState.Success) {
                authContext.login(((LoginUIState.Success) result).getData());
                Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
                startMain();
            } else if (result instanceof LoginUIState.Error) {
                Toast.makeText(this, getString(((LoginUIState.Error) result).getMessageRes()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void afterTextChanged(Editable s) { }
    }
}