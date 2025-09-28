package com.example.support_management_system_mobile.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.utils.AuthContext;
import com.example.support_management_system_mobile.data.models.Event;
import com.example.support_management_system_mobile.data.models.User;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends ViewModel {
    private final Application application;

    @Inject
    AuthContext authContext;

    private final MutableLiveData<ProfileUIState> _screenState = new MutableLiveData<>();
    public LiveData<ProfileUIState> getScreenState() {
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
    public ProfileViewModel(@NonNull Application application) {
        this.application = application;
    }

    public void refreshUserData() {
        _screenState.setValue(new ProfileUIState.Loading());

        if (!authContext.isLoggedIn()) {
            _navigateToLogin.setValue(new Event<>(true));
            return;
        }

        User userData = authContext.getCurrentUser();
        String fullName = application.getString(R.string.full_name_format, userData.name(), userData.surname());
        boolean isOperatorOrAdmin = authContext.isOperatorOrAdmin();

        _screenState.setValue(new ProfileUIState.Success(
                userData.username(),
                fullName,
                userData.email(),
                getRoleStringRes(authContext.getUserRole()),
                isOperatorOrAdmin
        ));
    }

    public void onLogoutClicked() {
        authContext.logout();
        _navigateToLogin.setValue(new Event<>(true));
    }

    public void onEditProfileClicked() {
        _navigateToEditProfile.setValue(new Event<>(true));
    }

    @StringRes
    private int getRoleStringRes(String role) {
        if (role == null) return R.string.role_unknown;
        return switch (role) {
            case "ROLE_USER" -> R.string.role_user;
            case "ROLE_OPERATOR" -> R.string.role_operator;
            case "ROLE_ADMIN" -> R.string.role_admin;
            default -> R.string.role_unknown;
        };
    }

    private final MutableLiveData<Event<Boolean>> _navigateToManagementPanel = new MutableLiveData<>();

    public void onManagementPanelClicked() {
        _navigateToManagementPanel.setValue(new Event<>(true));
    }

    public LiveData<Event<Boolean>> getNavigateToManagementPanel() {
        return _navigateToManagementPanel;
    }
}
