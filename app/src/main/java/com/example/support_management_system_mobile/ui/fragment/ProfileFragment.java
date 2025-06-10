package com.example.support_management_system_mobile.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.support_management_system_mobile.MainActivity;
import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.auth.JWTUtils;
import com.example.support_management_system_mobile.ui.activity.LoginActivity;

public class ProfileFragment extends Fragment {
    private TextView usernameText, fullNameText, roleText, emailText;
    private Button logoutButton, changePasswordButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (JWTUtils.getToken(requireContext()) == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameText = view.findViewById(R.id.usernameText);
        fullNameText = view.findViewById(R.id.fullNameText);
        roleText = view.findViewById(R.id.roleText);
        emailText = view.findViewById(R.id.emailText);

        logoutButton = view.findViewById(R.id.logoutButton);
        changePasswordButton = view.findViewById(R.id.changePasswordButton);
        setupButtonListeners();

        setDisplayedUserData();

        return view;
    }

    private void setDisplayedUserData(){
        usernameText.setText(JWTUtils.getUsername(requireContext()));
        emailText.setText(JWTUtils.getEmail(requireContext()));

        String name = JWTUtils.getName(requireContext());
        String surname = JWTUtils.getSurname(requireContext());
        String fullName = getString(R.string.full_name_format, name, surname);
        fullNameText.setText(fullName);

        String userRole = JWTUtils.getUserRole(requireContext());
        roleText.setText(getRoleString(userRole));
    }

    private void setupButtonListeners() {
        logoutButton.setOnClickListener(v -> {
            JWTUtils.clearData(requireContext());
            startActivity(new Intent(getActivity(), MainActivity.class));
            requireActivity().finish();
        });

        changePasswordButton.setOnClickListener(v -> {
            Fragment editProfileFragment = new EditProfileFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContainer, editProfileFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private int getRoleString(String role) {
        switch (role) {
            case "ROLE_USER":
                return R.string.role_user;
            case "ROLE_OPERATOR":
                return R.string.role_operator;
            case "ROLE_ADMIN":
                return R.string.role_admin;
            default:
                return R.string.role_unknown;
        }
    }
}