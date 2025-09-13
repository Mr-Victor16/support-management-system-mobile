package com.example.support_management_system_mobile.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.data.repository.AuthRepository;
import com.example.support_management_system_mobile.payload.request.LoginRequest;
import com.example.support_management_system_mobile.payload.response.LoginResponse;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class LoginViewModel extends AndroidViewModel {
    private final AuthRepository repository;

    public final MutableLiveData<String> username = new MutableLiveData<>("");
    public final MutableLiveData<String> password = new MutableLiveData<>("");

    private final MutableLiveData<LoginResult> result = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private final MediatorLiveData<Boolean> isLoginButtonEnabled = new MediatorLiveData<>();

    @Inject
    public LoginViewModel(@NonNull Application app, AuthRepository repository) {
        super(app);
        this.repository = repository;

        isLoginButtonEnabled.addSource(username, value -> validateForm());
        isLoginButtonEnabled.addSource(password, value -> validateForm());
        isLoginButtonEnabled.addSource(isLoading, value -> validateForm());
    }

    private void validateForm() {
        String user = username.getValue();
        String pass = password.getValue();
        boolean loading = Boolean.TRUE.equals(isLoading.getValue());

        boolean isFormValid = user != null && !user.trim().isEmpty() &&
                pass != null && !pass.trim().isEmpty();

        isLoginButtonEnabled.setValue(isFormValid && !loading);
    }

    public void loginUser() {
        if (Boolean.FALSE.equals(isLoginButtonEnabled.getValue())) return;

        isLoading.setValue(true);
        LoginRequest req = new LoginRequest(username.getValue(), password.getValue());

        repository.login(req, new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    result.postValue(new LoginResult.Success(resp.body()));
                } else {
                    result.postValue(new LoginResult.Error(R.string.invalid_username_or_password));
                }
                isLoading.postValue(false);
            }
            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                result.postValue(new LoginResult.Error(R.string.server_error));
                isLoading.postValue(false);
            }
        });
    }

    public LiveData<LoginResult> getResult() {
        return result;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsLoginButtonEnabled() {
        return isLoginButtonEnabled;
    }
}