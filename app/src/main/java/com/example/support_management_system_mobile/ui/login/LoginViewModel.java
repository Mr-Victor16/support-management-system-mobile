package com.example.support_management_system_mobile.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.data.repository.AuthRepository;
import com.example.support_management_system_mobile.data.payload.request.LoginRequest;
import com.example.support_management_system_mobile.data.payload.response.LoginResponse;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class LoginViewModel extends AndroidViewModel {
    private final AuthRepository repository;

    private final MutableLiveData<String> _username = new MutableLiveData<>("");
    private final MutableLiveData<String> _password = new MutableLiveData<>("");
    private final MutableLiveData<LoginUIState> _result = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final MediatorLiveData<Boolean> _isLoginButtonEnabled = new MediatorLiveData<>();

    public LiveData<LoginUIState> getResult() {
        return _result;
    }

    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }

    public LiveData<Boolean> getIsLoginButtonEnabled() {
        return _isLoginButtonEnabled;
    }

    @Inject
    public LoginViewModel(@NonNull Application app, AuthRepository repository) {
        super(app);
        this.repository = repository;

        _isLoginButtonEnabled.addSource(_username, value -> validateForm());
        _isLoginButtonEnabled.addSource(_password, value -> validateForm());
        _isLoginButtonEnabled.addSource(_isLoading, value -> validateForm());
    }

    public void onUsernameChanged(String username) {
        _username.setValue(username);
    }

    public void onPasswordChanged(String password) {
        _password.setValue(password);
    }

    private void validateForm() {
        String user = _username.getValue();
        String pass = _password.getValue();
        boolean loading = Boolean.TRUE.equals(_isLoading.getValue());

        boolean isFormValid = user != null && !user.trim().isEmpty() &&
                pass != null && !pass.trim().isEmpty();

        _isLoginButtonEnabled.setValue(isFormValid && !loading);
    }

    public void loginUser() {
        if (Boolean.FALSE.equals(_isLoginButtonEnabled.getValue())) return;

        _isLoading.setValue(true);
        LoginRequest request = new LoginRequest(_username.getValue(), _password.getValue());

        repository.login(request, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    _result.postValue(new LoginUIState.Success(resp.body()));
                } else {
                    _result.postValue(new LoginUIState.Error(R.string.invalid_username_or_password));
                }
                _isLoading.postValue(false);
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                _result.postValue(new LoginUIState.Error(R.string.server_error));
                _isLoading.postValue(false);
            }
        });
    }
}