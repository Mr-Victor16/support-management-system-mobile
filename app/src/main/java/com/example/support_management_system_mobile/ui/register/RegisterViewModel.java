package com.example.support_management_system_mobile.ui.register;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.data.repository.AuthRepository;
import com.example.support_management_system_mobile.data.payload.request.RegisterRequest;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class RegisterViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;

    private final MutableLiveData<String> _username = new MutableLiveData<>("");
    private final MutableLiveData<String> _name = new MutableLiveData<>("");
    private final MutableLiveData<String> _surname = new MutableLiveData<>("");
    private final MutableLiveData<String> _email = new MutableLiveData<>("");
    private final MutableLiveData<String> _password = new MutableLiveData<>("");

    private final MediatorLiveData<RegisterFormState> _formState = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<RegisterUIState> _result = new MutableLiveData<>();
    private final MediatorLiveData<Boolean> _isRegisterButtonEnabled = new MediatorLiveData<>();

    public LiveData<RegisterFormState> getFormState() { return _formState; }
    public LiveData<Boolean> getIsLoading() { return _isLoading; }
    public LiveData<RegisterUIState> getResult() { return _result; }
    public LiveData<Boolean> getIsRegisterButtonEnabled() { return _isRegisterButtonEnabled; }

    @Inject
    public RegisterViewModel(@NonNull Application application, AuthRepository authRepository) {
        super(application);
        this.authRepository = authRepository;

        _formState.addSource(_username, text -> validateForm());
        _formState.addSource(_name, text -> validateForm());
        _formState.addSource(_surname, text -> validateForm());
        _formState.addSource(_email, text -> validateForm());
        _formState.addSource(_password, text -> validateForm());

        _isRegisterButtonEnabled.addSource(_formState, state -> updateButtonState());
        _isRegisterButtonEnabled.addSource(_isLoading, loading -> updateButtonState());
    }

    public void onUsernameChanged(String username) { _username.setValue(username); }
    public void onNameChanged(String name) { _name.setValue(name); }
    public void onSurnameChanged(String surname) { _surname.setValue(surname); }
    public void onEmailChanged(String email) { _email.setValue(email); }
    public void onPasswordChanged(String password) { _password.setValue(password); }

    private void updateButtonState() {
        RegisterFormState state = _formState.getValue();
        boolean loading = Boolean.TRUE.equals(_isLoading.getValue());
        boolean enabled = state != null && state.isDataValid() && !loading;
        _isRegisterButtonEnabled.setValue(enabled);
    }

    private void validateForm() {
        _formState.setValue(
                RegisterFormState.create(
                        Objects.requireNonNullElse(_username.getValue(), ""),
                        Objects.requireNonNullElse(_name.getValue(), ""),
                        Objects.requireNonNullElse(_surname.getValue(), ""),
                        Objects.requireNonNullElse(_email.getValue(), ""),
                        Objects.requireNonNullElse(_password.getValue(), "")
                )
        );
    }

    public void register() {
        RegisterFormState currentState = _formState.getValue();
        if (currentState == null || !currentState.isDataValid()) {
            return;
        }

        _isLoading.setValue(true);
        RegisterRequest request = new RegisterRequest(
                _username.getValue(), _password.getValue(), _email.getValue(), _name.getValue(), _surname.getValue()
        );

        authRepository.register(request, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    _result.postValue(new RegisterUIState.Success());
                } else {
                    _result.postValue(new RegisterUIState.Error(R.string.invalid_data_register_error));
                }
                _isLoading.postValue(false);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                _result.postValue(new RegisterUIState.Error(R.string.server_error));
                _isLoading.postValue(false);
            }
        });
    }
}