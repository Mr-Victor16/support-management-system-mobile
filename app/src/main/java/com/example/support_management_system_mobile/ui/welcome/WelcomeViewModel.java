package com.example.support_management_system_mobile.ui.welcome;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.auth.JWTUtils;
import com.example.support_management_system_mobile.models.Event;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WelcomeViewModel extends ViewModel {
    private final Application application;

    private final MutableLiveData<WelcomeUIState> _uiState = new MutableLiveData<>();
    public final LiveData<WelcomeUIState> uiState = _uiState;

    private final MutableLiveData<Event<NavigationTarget>> _navigation = new MutableLiveData<>();
    public final LiveData<Event<NavigationTarget>> navigation = _navigation;

    @Inject
    public WelcomeViewModel(@NonNull Application application) {
        this.application = application;
    }

    public void loadData() {
        _uiState.setValue(new WelcomeUIState.Loading());

        if (JWTUtils.getToken(application) == null) {
            String welcomeText = application.getString(R.string.welcome);
            _uiState.setValue(new WelcomeUIState.Success(welcomeText, true));
        } else {
            String name = JWTUtils.getName(application);
            String welcomeText = application.getString(R.string.welcome_header_format, name);
            _uiState.setValue(new WelcomeUIState.Success(welcomeText, false));
        }
    }

    public void onLoginClicked() {
        _navigation.setValue(new Event<>(NavigationTarget.LOGIN));
    }

    public void onSoftwareClicked() {
        _navigation.setValue(new Event<>(NavigationTarget.SUPPORTED_SOFTWARE));
    }

    public void onKnowledgeClicked() {
        _navigation.setValue(new Event<>(NavigationTarget.KNOWLEDGE));
    }

}
