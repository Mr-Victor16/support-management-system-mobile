package com.example.support_management_system_mobile.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.utils.AuthContext;
import com.example.support_management_system_mobile.ui.login.LoginActivity;
import com.example.support_management_system_mobile.ui.profile.ProfileFragment;
import com.example.support_management_system_mobile.ui.ticket.list.TicketListFragment;
import com.example.support_management_system_mobile.ui.welcome.WelcomeFragment;
import com.google.android.material.navigation.NavigationBarView;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    @Inject
    AuthContext authContext;

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

        setupNavigation();

        if (savedInstanceState == null) {
            loadFragment(R.id.nav_main);
        }
    }

    private void setupNavigation() {
        NavigationBarView navView = findViewById(R.id.mainNavigationBar);
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_tickets || itemId == R.id.nav_account) {
                if (!authContext.isLoggedIn()) {
                    startActivity(new Intent(this, LoginActivity.class));

                    return false;
                }
            }

            loadFragment(itemId);
            return true;
        });
    }

    private void loadFragment(@IdRes int itemId) {
        Fragment fragment;

        if (itemId == R.id.nav_main) {
            fragment = new WelcomeFragment();
        } else if (itemId == R.id.nav_tickets) {
            fragment = new TicketListFragment();
        } else if (itemId == R.id.nav_account) {
            fragment = new ProfileFragment();
        } else {
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .commit();
    }
}