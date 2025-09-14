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
import com.example.support_management_system_mobile.utils.validators.UserValidator;

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
        String usernameValue = _username.getValue();
        Integer usernameError = (usernameValue != null && !usernameValue.isEmpty() && !UserValidator.isUsernameValid(usernameValue))
                ? R.string.username_register_error : null;

        String nameValue = _name.getValue();
        Integer nameError = (nameValue != null && !nameValue.isEmpty() && !UserValidator.isNameValid(nameValue))
                ? R.string.name_register_error : null;

        String surnameValue = _surname.getValue();
        Integer surnameError = (surnameValue != null && !surnameValue.isEmpty() && !UserValidator.isSurnameValid(surnameValue))
                ? R.string.surname_register_error : null;

        String emailValue = _email.getValue();
        Integer emailError = (emailValue != null && !emailValue.isEmpty() && !UserValidator.isEmailValid(emailValue))
                ? R.string.email_register_error : null;

        String passwordValue = _password.getValue();
        Integer passwordError = (passwordValue != null && !passwordValue.isEmpty() && !UserValidator.isPasswordValid(passwordValue))
                ? R.string.password_register_error : null;

        boolean isDataValidForSubmission = UserValidator.isUsernameValid(usernameValue) &&
                UserValidator.isNameValid(nameValue) &&
                UserValidator.isSurnameValid(surnameValue) &&
                UserValidator.isEmailValid(emailValue) &&
                UserValidator.isPasswordValid(passwordValue);

        _formState.setValue(new RegisterFormState(usernameError, nameError, surnameError, emailError, passwordError, isDataValidForSubmission));
    }

    public void register() {
        validateForm();

        RegisterFormState currentState = _formState.getValue();
        if (currentState == null || !currentState.isDataValid()) {
            return;
        }

        _isLoading.setValue(true);
        RegisterRequest request = new RegisterRequest(
                _username.getValue(), _password.getValue(), _email.getValue(), _name.getValue(), _surname.getValue()
        );

        authRepository.register(request, new Callback<String>() {
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