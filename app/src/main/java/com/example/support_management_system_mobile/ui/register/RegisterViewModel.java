package com.example.support_management_system_mobile.ui.register;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.data.repository.AuthRepository;
import com.example.support_management_system_mobile.payload.request.RegisterRequest;
import com.example.support_management_system_mobile.validators.UserValidator;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class RegisterViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;

    public final MutableLiveData<String> username = new MutableLiveData<>("");
    public final MutableLiveData<String> name = new MutableLiveData<>("");
    public final MutableLiveData<String> surname = new MutableLiveData<>("");
    public final MutableLiveData<String> email = new MutableLiveData<>("");
    public final MutableLiveData<String> password = new MutableLiveData<>("");

    private final MutableLiveData<Boolean> isUsernameTouched = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isNameTouched = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSurnameTouched = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isEmailTouched = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isPasswordTouched = new MutableLiveData<>(false);


    private final MediatorLiveData<RegisterFormState> formState = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<RegisterResult> result = new MutableLiveData<>();

    @Inject
    public RegisterViewModel(@NonNull Application application, AuthRepository authRepository) {
        super(application);
        this.authRepository = authRepository;

        formState.addSource(username, text -> validateForm());
        formState.addSource(name, text -> validateForm());
        formState.addSource(surname, text -> validateForm());
        formState.addSource(email, text -> validateForm());
        formState.addSource(password, text -> validateForm());

        formState.addSource(isUsernameTouched, touched -> validateForm());
        formState.addSource(isNameTouched, touched -> validateForm());
        formState.addSource(isSurnameTouched, touched -> validateForm());
        formState.addSource(isEmailTouched, touched -> validateForm());
        formState.addSource(isPasswordTouched, touched -> validateForm());
    }

    private void validateForm() {
        Integer usernameError = null;
        if (Boolean.TRUE.equals(isUsernameTouched.getValue()) && !UserValidator.isUsernameValid(username.getValue())) {
            usernameError = R.string.username_register_error;
        }

        Integer nameError = null;
        if (Boolean.TRUE.equals(isNameTouched.getValue()) && !UserValidator.isNameValid(name.getValue())) {
            nameError = R.string.name_register_error;
        }

        Integer surnameError = null;
        if (Boolean.TRUE.equals(isSurnameTouched.getValue()) && !UserValidator.isSurnameValid(surname.getValue())) {
            surnameError = R.string.surname_register_error;
        }

        Integer emailError = null;
        if (Boolean.TRUE.equals(isEmailTouched.getValue()) && !UserValidator.isEmailValid(email.getValue())) {
            emailError = R.string.email_register_error;
        }

        Integer passwordError = null;
        if (Boolean.TRUE.equals(isPasswordTouched.getValue()) && !UserValidator.isPasswordValid(password.getValue())) {
            passwordError = R.string.password_register_error;
        }

        boolean isDataValidForSubmission = UserValidator.isUsernameValid(username.getValue()) &&
                UserValidator.isNameValid(name.getValue()) &&
                UserValidator.isSurnameValid(surname.getValue()) &&
                UserValidator.isEmailValid(email.getValue()) &&
                UserValidator.isPasswordValid(password.getValue());

        formState.setValue(new RegisterFormState(usernameError, nameError, surnameError, emailError, passwordError, isDataValidForSubmission));
    }

    public void register() {
        RegisterFormState currentState = formState.getValue();
        if (currentState == null || !currentState.isDataValid()) {
            isUsernameTouched.setValue(true);
            isNameTouched.setValue(true);
            isSurnameTouched.setValue(true);
            isEmailTouched.setValue(true);
            isPasswordTouched.setValue(true);
            validateForm();
            return;
        }

        isLoading.setValue(true);
        RegisterRequest request = new RegisterRequest(
                username.getValue(), password.getValue(), email.getValue(), name.getValue(), surname.getValue()
        );

        authRepository.register(request, new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    result.postValue(new RegisterResult.Success());
                } else {
                    result.postValue(new RegisterResult.Error(R.string.invalid_data_register_error));
                }
                isLoading.postValue(false);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                result.postValue(new RegisterResult.Error(R.string.server_error));
                isLoading.postValue(false);
            }
        });
    }

    public void onUsernameFocusLost() { isUsernameTouched.setValue(true); }
    public void onNameFocusLost() { isNameTouched.setValue(true); }
    public void onSurnameFocusLost() { isSurnameTouched.setValue(true); }
    public void onEmailFocusLost() { isEmailTouched.setValue(true); }
    public void onPasswordFocusLost() { isPasswordTouched.setValue(true); }

    public LiveData<RegisterFormState> getFormState() { return formState; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<RegisterResult> getResult() { return result; }
}