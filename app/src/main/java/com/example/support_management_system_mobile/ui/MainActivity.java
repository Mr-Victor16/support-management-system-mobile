package com.example.support_management_system_mobile.ui;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.ui.profile.ProfileFragment;
import com.example.support_management_system_mobile.ui.ticket.TicketListFragment;
import com.example.support_management_system_mobile.ui.welcome.WelcomeFragment;
import com.google.android.material.navigation.NavigationBarView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
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

        setupNavigation();

        if (savedInstanceState == null) {
            loadFragment(R.id.nav_main);
        }
    }

    private void setupNavigation() {
        View navView = findViewById(R.id.mainNavigationBar);
        if (navView instanceof NavigationBarView) {
            ((NavigationBarView) navView).setOnItemSelectedListener(item -> {
                loadFragment(item.getItemId());
                return true;
            });
        }
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