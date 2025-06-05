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
import com.example.support_management_system_mobile.payload.request.RegisterRequest;

import com.example.support_management_system_mobile.validators.RegisterValidator;

import java.util.HashMap;
import java.util.function.Predicate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameEditText, nameEditText, surnameEditText, emailEditText, passwordEditText;
    private Handler handler;
    private Button registerButton;
    private final HashMap<EditText, Boolean> visitedFields = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        handler = new Handler();

        if(JWTUtils.getToken(this) != null){
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        }

        usernameEditText = findViewById(R.id.editUsername);
        nameEditText = findViewById(R.id.editName);
        surnameEditText = findViewById(R.id.editSurname);
        emailEditText = findViewById(R.id.editEmail);
        passwordEditText = findViewById(R.id.editPassword);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setEnabled(false);
        registerButton.setOnClickListener(v -> registerUser());

        setupTextListeners();
    }

    private void setupTextListeners() {
        for (EditText editText : new EditText[]{usernameEditText, nameEditText, surnameEditText, emailEditText, passwordEditText}) {
            editText.addTextChangedListener(createTextWatcher(editText));
        }
    }

    private void registerUser() {
        String login = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String surname = surnameEditText.getText().toString();
        String email = emailEditText.getText().toString();

        RegisterRequest request = new RegisterRequest(login, password, email, name, surname);

        APIClient.getAPIService().register(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, R.string.successfully_registered, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, R.string.invalid_data_register_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Toast.makeText(RegisterActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private TextWatcher createTextWatcher(EditText editText) {
        return new TextWatcher() {
            private Runnable runnable;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!visitedFields.containsKey(editText)) {
                    visitedFields.put(editText, true);
                }

                if (runnable != null) handler.removeCallbacks(runnable);
                runnable = () -> validateForm();
                handler.postDelayed(runnable, 300);
            }

            @Override public void afterTextChanged(Editable s) {}
        };
    }

    private void validateForm() {
        boolean valid = true;

        valid &= validateField(usernameEditText, RegisterValidator::isUsernameValid, R.string.username_register_error);
        valid &= validateField(nameEditText, RegisterValidator::isNameValid, R.string.name_register_error);
        valid &= validateField(surnameEditText, RegisterValidator::isSurnameValid, R.string.surname_register_error);
        valid &= validateField(emailEditText, RegisterValidator::isEmailValid, R.string.email_register_error);
        valid &= validateField(passwordEditText, RegisterValidator::isPasswordValid, R.string.password_register_error);

        registerButton.setEnabled(valid);
    }

    private boolean validateField(EditText field, Predicate<String> validator, int errorMsgResId) {
        String input = field.getText().toString().trim();
        boolean isValid = validator.test(input);

        if (!isValid && Boolean.TRUE.equals(visitedFields.get(field))) {
            field.setError(getString(errorMsgResId));
        } else {
            field.setError(null);
        }

        return isValid;
    }
}