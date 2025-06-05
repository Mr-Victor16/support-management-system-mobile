package com.example.support_management_system_mobile.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.support_management_system_mobile.MainActivity;
import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.auth.APIClient;
import com.example.support_management_system_mobile.auth.JWTUtils;
import com.example.support_management_system_mobile.payload.request.LoginRequest;
import com.example.support_management_system_mobile.payload.response.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText editUsername, editPassword;
    private Button loginButton;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        handler = new Handler();

        if(JWTUtils.getToken(this) != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        setupTextListeners();

        loginButton = findViewById(R.id.loginButton);
        loginButton.setEnabled(false);
        loginButton.setOnClickListener(v -> loginUser());

        Button createAccountButton = findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void setupTextListeners() {
        TextWatcher watcher = createTextWatcher();
        editUsername.addTextChangedListener(watcher);
        editPassword.addTextChangedListener(watcher);
    }

    private void loginUser() {
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();

        LoginRequest request = new LoginRequest(username, password);

        APIClient.getAPIService().login(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JWTUtils.saveData(LoginActivity.this, response.body());

                    Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.invalid_username_or_password, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private TextWatcher createTextWatcher() {
        return new TextWatcher() {
            private Runnable runnable;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (runnable != null) handler.removeCallbacks(runnable);
                runnable = () -> validateForm();
                handler.postDelayed(runnable, 300);
            }

            @Override public void afterTextChanged(Editable s) {}
        };
    }

    private void validateForm() {
        boolean isValid = !editUsername.getText().toString().trim().isEmpty()
                && !editPassword.getText().toString().trim().isEmpty();

        loginButton.setEnabled(isValid);
    }
}