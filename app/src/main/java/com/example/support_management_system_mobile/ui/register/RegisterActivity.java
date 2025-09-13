package com.example.support_management_system_mobile.ui.register;

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
import com.example.support_management_system_mobile.databinding.ActivityRegisterBinding;
import com.example.support_management_system_mobile.ui.login.LoginActivity;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private RegisterViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (JWTUtils.getToken(this) != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        vm = new ViewModelProvider(this).get(RegisterViewModel.class);

        setupInputListeners();
        setupObservers();

        binding.registerButton.setOnClickListener(v -> vm.register());
    }

    private void setupInputListeners() {
        binding.editUsername.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vm.username.setValue(s.toString());
            }
        });

        binding.editName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vm.name.setValue(s.toString());
            }
        });

        binding.editSurname.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vm.surname.setValue(s.toString());
            }
        });

        binding.editEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vm.email.setValue(s.toString());
            }
        });

        binding.editPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vm.password.setValue(s.toString());
            }
        });

        binding.editUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) vm.onUsernameFocusLost();
        });

        binding.editName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) vm.onNameFocusLost();
        });

        binding.editSurname.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) vm.onSurnameFocusLost();
        });

        binding.editEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) vm.onEmailFocusLost();
        });

        binding.editPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) vm.onPasswordFocusLost();
        });
    }

    private void setupObservers() {
        vm.getFormState().observe(this, state -> {
            if (state == null) return;

            if (state.getUsernameError() != null) {
                if (!Objects.equals(binding.editUsername.getError(), getString(state.getUsernameError()))) {
                    binding.editUsername.setError(getString(state.getUsernameError()));
                }
            } else {
                binding.editUsername.setError(null);
            }

            if (state.getNameError() != null) {
                if (!Objects.equals(binding.editName.getError(), getString(state.getNameError()))) {
                    binding.editName.setError(getString(state.getNameError()));
                }
            } else {
                binding.editName.setError(null);
            }

            if (state.getSurnameError() != null) {
                if (!Objects.equals(binding.editSurname.getError(), getString(state.getSurnameError()))) {
                    binding.editSurname.setError(getString(state.getSurnameError()));
                }
            } else {
                binding.editSurname.setError(null);
            }

            if (state.getEmailError() != null) {
                if (!Objects.equals(binding.editEmail.getError(), getString(state.getEmailError()))) {
                    binding.editEmail.setError(getString(state.getEmailError()));
                }
            } else {
                binding.editEmail.setError(null);
            }

            if (state.getPasswordError() != null) {
                if (!Objects.equals(binding.editPassword.getError(), getString(state.getPasswordError()))) {
                    binding.editPassword.setError(getString(state.getPasswordError()));
                }
            } else {
                binding.editPassword.setError(null);
            }

            boolean isLoading = vm.getIsLoading().getValue() != null && vm.getIsLoading().getValue();
            binding.registerButton.setEnabled(state.isDataValid() && !isLoading);
        });

        vm.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (vm.getFormState().getValue() != null) {
                binding.registerButton.setEnabled(!isLoading && vm.getFormState().getValue().isDataValid());
            }
        });

        vm.getResult().observe(this, result -> {
            if (result instanceof RegisterResult.Success) {
                Toast.makeText(this, R.string.successfully_registered, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else if (result instanceof RegisterResult.Error) {
                Toast.makeText(this, getString(((RegisterResult.Error) result).getMessageRes()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }
}