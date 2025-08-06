package com.example.support_management_system_mobile.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.support_management_system_mobile.ui.MainActivity;
import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.auth.JWTUtils;
import com.example.support_management_system_mobile.databinding.ActivityLoginBinding;
import com.example.support_management_system_mobile.ui.register.RegisterActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (JWTUtils.getToken(this) != null) {
            startMain();
            return;
        }

        vm = new ViewModelProvider(this).get(LoginViewModel.class);

        setupInputListeners();
        setupObservers();

        binding.loginButton.setOnClickListener(v -> vm.loginUser());
        binding.createAccountButton.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void setupInputListeners() {
        binding.editUsername.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vm.username.setValue(s.toString());
            }
        });

        binding.editPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vm.password.setValue(s.toString());
            }
        });
    }

    private void setupObservers() {
        vm.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.createAccountButton.setEnabled(!isLoading);
        });

        vm.getIsLoginButtonEnabled().observe(this, isEnabled -> binding.loginButton.setEnabled(isEnabled));

        vm.getResult().observe(this, result -> {
            if (result instanceof LoginResult.Success) {
                JWTUtils.saveData(this, ((LoginResult.Success) result).getData());
                Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
                startMain();
            } else if (result instanceof LoginResult.Error) {
                Toast.makeText(this, getString(((LoginResult.Error) result).getMessageRes()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}