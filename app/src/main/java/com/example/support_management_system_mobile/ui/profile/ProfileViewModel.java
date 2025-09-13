package com.example.support_management_system_mobile.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.auth.AuthContext;
import com.example.support_management_system_mobile.auth.JWTUtils;
import com.example.support_management_system_mobile.models.Event;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends ViewModel {
    private final Application application;
    private final AuthContext authContext;

    public static class ProfileUIState {
        public final String username;
        public final String fullName;
        public final String email;

        @StringRes
        public final int roleResId;

        final boolean isManagementPanelVisible;

        ProfileUIState(String username, String fullName, String email, @StringRes int roleResId, boolean isManagementPanelVisible) {
            this.username = username;
            this.fullName = fullName;
            this.email = email;
            this.roleResId = roleResId;
            this.isManagementPanelVisible = isManagementPanelVisible;
        }
    }

    private final MutableLiveData<ProfileScreenState> _screenState = new MutableLiveData<>();
    public LiveData<ProfileScreenState> getScreenState() {
        return _screenState;
    }

    private final MutableLiveData<Event<Boolean>> _navigateToLogin = new MutableLiveData<>();
    public LiveData<Event<Boolean>> getNavigateToLogin() {
        return _navigateToLogin;
    }

    private final MutableLiveData<Event<Boolean>> _navigateToEditProfile = new MutableLiveData<>();
    public LiveData<Event<Boolean>> getNavigateToEditProfile() {
        return _navigateToEditProfile;
    }

    @Inject
    public ProfileViewModel(@NonNull Application application, AuthContext authContext) {
        this.application = application;
        this.authContext = authContext;
    }

    public void refreshUserData() {
        _screenState.setValue(new ProfileScreenState.Loading());

        if (JWTUtils.getToken(application) == null) {
            _navigateToLogin.setValue(new Event<>(true));
            return;
        }

        String username = JWTUtils.getUsername(application);
        String email = JWTUtils.getEmail(application);
        String name = JWTUtils.getName(application);
        String surname = JWTUtils.getSurname(application);
        String fullName = application.getString(R.string.full_name_format, name, surname);
        String userRole = JWTUtils.getUserRole(application);
        boolean isOperatorOrAdmin = authContext.isOperatorOrAdmin();

        ProfileUIState userData = new ProfileUIState(username, fullName, email, getRoleStringRes(userRole), isOperatorOrAdmin);
        _screenState.setValue(new ProfileScreenState.Success(userData));
    }

    public void onLogoutClicked() {
        JWTUtils.clearData(application);
        _navigateToLogin.setValue(new Event<>(true));
    }

    public void onEditProfileClicked() {
        _navigateToEditProfile.setValue(new Event<>(true));
    }

    @StringRes
    private int getRoleStringRes(String role) {
        if (role == null) return R.string.role_unknown;
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

    private final MutableLiveData<Event<Boolean>> _navigateToManagementPanel = new MutableLiveData<>();

    public void onManagementPanelClicked() {
        _navigateToManagementPanel.setValue(new Event<>(true));
    }

    public LiveData<Event<Boolean>> getNavigateToManagementPanel() {
        return _navigateToManagementPanel;
    }
}
