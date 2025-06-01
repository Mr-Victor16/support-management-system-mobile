package com.example.support_management_system_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.support_management_system_mobile.activity.LoginActivity;
import com.example.support_management_system_mobile.auth.JWTUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String token = JWTUtils.getToken(this);
        if (token == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
        }
    }
}