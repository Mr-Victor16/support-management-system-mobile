package com.example.support_management_system_mobile.ui.profile.edit;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.utils.AuthContext;
import com.example.support_management_system_mobile.data.repository.ProfileRepository;
import com.example.support_management_system_mobile.data.payload.request.update.UpdateProfileRequest;
import com.example.support_management_system_mobile.utils.validators.UserValidator;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class EditProfileViewModel extends ViewModel {
    private final ProfileRepository profileRepository;

    private String originalFirstName;
    private String originalSurname;

    @Inject
    AuthContext authContext;

    public final MutableLiveData<String> firstName = new MutableLiveData<>();
    public final MutableLiveData<String> surname = new MutableLiveData<>();
    public final MutableLiveData<String> password = new MutableLiveData<>();

    private final MediatorLiveData<EditProfileFormState> formState = new MediatorLiveData<>();
    private final MutableLiveData<EditProfileUIState> updateResult = new MutableLiveData<>();

    @Inject
    public EditProfileViewModel(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;

        formState.addSource(firstName, value -> validateForm());
        formState.addSource(surname, value -> validateForm());
        formState.addSource(password, value -> validateForm());
    }

    public void start(String originalFirstName, String originalSurname) {
        this.originalFirstName = originalFirstName;
        this.originalSurname = originalSurname;

        firstName.setValue(originalFirstName);
        surname.setValue(originalSurname);
        password.setValue("");
    }

    private void validateForm() {
        String fName = firstName.getValue();
        String sName = surname.getValue();
        String pwd = password.getValue();

        if (!UserValidator.isNameValid(fName)) {
            formState.setValue(new EditProfileFormState(R.string.name_register_error, null, null));
            return;
        }

        if (!UserValidator.isSurnameValid(sName)) {
            formState.setValue(new EditProfileFormState(null, R.string.surname_register_error, null));
            return;
        }

        if (pwd != null && !pwd.isEmpty() && !UserValidator.isPasswordValid(pwd)) {
            formState.setValue(new EditProfileFormState(null, null, R.string.password_register_error));
            return;
        }

        boolean hasChanges = !Objects.equals(fName, originalFirstName)
                || !Objects.equals(sName, originalSurname)
                || (UserValidator.isPasswordValid(pwd));

        formState.setValue(new EditProfileFormState(true, hasChanges));
    }

    public void saveChanges() {
        EditProfileFormState currentState = formState.getValue();
        if (currentState == null || !currentState.isDataValid() || !currentState.hasChanges()) {
            return;
        }

        updateResult.setValue(new EditProfileUIState.Loading());

        String fName = firstName.getValue();
        String sName = surname.getValue();
        String pwd = password.getValue();

        if (!UserValidator.isPasswordValid(pwd)) pwd = "";

        UpdateProfileRequest request = new UpdateProfileRequest(fName, sName, pwd);

        profileRepository.update(request, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    authContext.setFullName(fName, sName);
                    updateResult.postValue(new EditProfileUIState.Success(R.string.profile_updated_successfully));
                } else {
                    updateResult.postValue(new EditProfileUIState.Error(R.string.profile_update_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                updateResult.postValue(new EditProfileUIState.Error(R.string.server_error));
            }
        });
    }

    public LiveData<EditProfileFormState> getFormState() {
        return formState;
    }

    public LiveData<EditProfileUIState> getUpdateResult() {
        return updateResult;
    }
}
