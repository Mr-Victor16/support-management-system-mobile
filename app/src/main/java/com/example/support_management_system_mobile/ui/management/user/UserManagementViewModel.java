package com.example.support_management_system_mobile.ui.management.user;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.utils.AuthContext;
import com.example.support_management_system_mobile.data.repository.RoleRepository;
import com.example.support_management_system_mobile.data.repository.UserRepository;
import com.example.support_management_system_mobile.data.models.Event;
import com.example.support_management_system_mobile.data.models.Role;
import com.example.support_management_system_mobile.data.payload.request.add.AddUserRequest;
import com.example.support_management_system_mobile.data.payload.request.update.UpdateUserRequest;
import com.example.support_management_system_mobile.data.payload.response.UserDetailsResponse;
import com.example.support_management_system_mobile.utils.validators.UserValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class UserManagementViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final Application application;
    private final AuthContext authContext;
    private Long currentEditingUserId = null;

    public enum FormField { USERNAME, NAME, SURNAME, EMAIL, PASSWORD }
    private final Set<FormField> interactedFields = new HashSet<>();

    public final MutableLiveData<String> username = new MutableLiveData<>("");
    public final MutableLiveData<String> name = new MutableLiveData<>("");
    public final MutableLiveData<String> surname = new MutableLiveData<>("");
    public final MutableLiveData<String> email = new MutableLiveData<>("");
    public final MutableLiveData<String> password = new MutableLiveData<>("");
    public final MutableLiveData<Role> selectedRole = new MutableLiveData<>();

    private final MutableLiveData<Integer> _usernameError = new MutableLiveData<>(null);
    public final LiveData<Integer> usernameError = _usernameError;
    private final MutableLiveData<Integer> _nameError = new MutableLiveData<>(null);
    public final LiveData<Integer> nameError = _nameError;
    private final MutableLiveData<Integer> _surnameError = new MutableLiveData<>(null);
    public final LiveData<Integer> surnameError = _surnameError;
    private final MutableLiveData<Integer> _emailError = new MutableLiveData<>(null);
    public final LiveData<Integer> emailError = _emailError;
    private final MutableLiveData<Integer> _passwordError = new MutableLiveData<>(null);
    public final LiveData<Integer> passwordError = _passwordError;

    private final MediatorLiveData<Boolean> _isFormValid = new MediatorLiveData<>();
    public final LiveData<Boolean> isFormValid = _isFormValid;

    private final MutableLiveData<List<Role>> _roleList = new MutableLiveData<>();
    public final LiveData<List<Role>> roleList = _roleList;

    private final MutableLiveData<UserFormUIState> _userFormState = new MutableLiveData<>();
    public final LiveData<UserFormUIState> userFormState = _userFormState;

    private final MutableLiveData<UserListUIState> _userListState = new MutableLiveData<>();
    public final LiveData<UserListUIState> userListState = _userListState;

    private final MutableLiveData<Event<String>> _toastMessage = new MutableLiveData<>();
    public final LiveData<Event<String>> toastMessage = _toastMessage;

    @Inject
    public UserManagementViewModel(Application application, UserRepository userRepository,
                                   AuthContext authContext, RoleRepository roleRepository) {
        this.application = application;
        this.userRepository = userRepository;
        this.authContext = authContext;
        this.roleRepository = roleRepository;

        _isFormValid.addSource(username, value -> validateUserForm());
        _isFormValid.addSource(name, value -> validateUserForm());
        _isFormValid.addSource(surname, value -> validateUserForm());
        _isFormValid.addSource(password, value -> validateUserForm());
        _isFormValid.addSource(email, value -> validateUserForm());
        _isFormValid.addSource(selectedRole, value -> validateUserForm());
    }

    public void loadUserList() {
        _userListState.setValue(new UserListUIState.Loading());

        userRepository.getUsers(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<UserDetailsResponse>> call, @NonNull Response<List<UserDetailsResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean canManage = authContext.isAdmin();
                    _userListState.postValue(new UserListUIState.Success(response.body(), canManage));
                } else {
                    _userListState.postValue(new UserListUIState.Error(application.getString(R.string.error_loading_data)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserDetailsResponse>> call, @NonNull Throwable t) {
                _userListState.postValue(new UserListUIState.Error(application.getString(R.string.server_error)));
            }
        });
    }

    public void deleteUser(long userId) {
        userRepository.deleteUser(userId, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.user_deleted_successfully)));
                    loadUserList();
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.user_delete_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public void createUser(AddUserRequest request) {
        userRepository.createUser(request, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.user_added_successfully)));
                    loadUserList();
                    _userFormState.postValue(new UserFormUIState.Success());
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.user_add_error)));

                    if (_userFormState.getValue() instanceof UserFormUIState.Submitting) {
                        _userFormState.postValue(new UserFormUIState.Editing(R.string.save));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));

                if (_userFormState.getValue() instanceof UserFormUIState.Submitting) {
                    _userFormState.postValue(new UserFormUIState.Editing(R.string.save));
                }
            }
        });
    }

    public void updateUser(UpdateUserRequest request) {
        userRepository.updateUser(request, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.user_updated_successfully)));
                    loadUserList();
                    _userFormState.postValue(new UserFormUIState.Success());
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.user_update_error)));

                    if (_userFormState.getValue() instanceof UserFormUIState.Submitting) {
                        _userFormState.postValue(new UserFormUIState.Editing(R.string.save_changes_button));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));

                if (_userFormState.getValue() instanceof UserFormUIState.Submitting) {
                    _userFormState.postValue(new UserFormUIState.Editing(R.string.save_changes_button));
                }
            }
        });
    }

    public AuthContext getAuthContext() {
        return authContext;
    }

    public void loadUserForm(Long userId) {
        this.currentEditingUserId = userId;
        _userFormState.setValue(new UserFormUIState.Loading());
        interactedFields.clear();

        roleRepository.getRoles(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Role>> call, @NonNull Response<List<Role>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    _roleList.postValue(response.body());
                    if (userId != null) {
                        loadUserForEditing(userId);
                    } else {
                        prepareNewUserForm();
                    }
                } else {
                    _userFormState.postValue(new UserFormUIState.Error(application.getString(R.string.form_data_load_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Role>> call, @NonNull Throwable t) {
                _userFormState.postValue(new UserFormUIState.Error(application.getString(R.string.server_error)));
            }
        });
    }

    private void loadUserForEditing(Long userId) {
        userRepository.getUserById(userId, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserDetailsResponse> call, @NonNull Response<UserDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDetailsResponse item = response.body();
                    username.postValue(item.username());
                    name.postValue(item.name());
                    surname.postValue(item.surname());
                    email.postValue(item.email());
                    selectedRole.postValue(item.role());

                    _userFormState.postValue(new UserFormUIState.Editing(R.string.save_changes_button));
                } else {
                    _userFormState.postValue(new UserFormUIState.Error(application.getString(R.string.error_loading_data)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDetailsResponse> call, @NonNull Throwable t) {
                _userFormState.postValue(new UserFormUIState.Error(application.getString(R.string.server_error)));
            }
        });
    }

    private void prepareNewUserForm() {
        username.postValue("");
        name.postValue("");
        surname.postValue("");
        password.postValue("");
        email.postValue("");

        selectedRole.postValue(null);
        _userFormState.postValue(new UserFormUIState.Editing(R.string.save));
        validateUserForm();
    }

    public void onFieldChanged(FormField field, String value) {
        interactedFields.add(field);

        switch (field) {
            case USERNAME:
                if (!java.util.Objects.equals(username.getValue(), value)) username.setValue(value);
                break;
            case NAME:
                if (!java.util.Objects.equals(name.getValue(), value)) name.setValue(value);
                break;
            case SURNAME:
                if (!java.util.Objects.equals(surname.getValue(), value)) surname.setValue(value);
                break;
            case EMAIL:
                if (!java.util.Objects.equals(email.getValue(), value)) email.setValue(value);
                break;
            case PASSWORD:
                if (!java.util.Objects.equals(password.getValue(), value)) password.setValue(value);
                break;
        }
    }

    private void validateUserForm() {
        boolean isUsernameValid = UserValidator.isUsernameValid(username.getValue());
        boolean isNameValid = UserValidator.isNameValid(name.getValue());
        boolean isSurnameValid = UserValidator.isSurnameValid(surname.getValue());
        boolean isEmailValid = UserValidator.isEmailValid(email.getValue());
        boolean isRoleSelected = selectedRole.getValue() != null;

        boolean isPasswordValid = true;
        if (currentEditingUserId == null) {
            isPasswordValid = UserValidator.isPasswordValid(password.getValue());
            _passwordError.setValue(interactedFields.contains(FormField.PASSWORD) && !isPasswordValid ? R.string.password_register_error : null);
        } else {
            _passwordError.setValue(null);
        }

        _usernameError.setValue(interactedFields.contains(FormField.USERNAME) && !isUsernameValid ? R.string.username_register_error : null);
        _nameError.setValue(interactedFields.contains(FormField.NAME) && !isNameValid ? R.string.name_register_error : null);
        _surnameError.setValue(interactedFields.contains(FormField.SURNAME) && !isSurnameValid ? R.string.surname_register_error : null);
        _emailError.setValue(interactedFields.contains(FormField.EMAIL) && !isEmailValid ? R.string.email_register_error : null);

        boolean isFieldValid = isUsernameValid && isNameValid && isSurnameValid && isEmailValid && isPasswordValid;
        _isFormValid.setValue(isFieldValid && isRoleSelected);
    }

    public void saveUser() {
        interactedFields.add(UserManagementViewModel.FormField.USERNAME);
        interactedFields.add(UserManagementViewModel.FormField.NAME);
        interactedFields.add(UserManagementViewModel.FormField.SURNAME);
        interactedFields.add(UserManagementViewModel.FormField.EMAIL);

        if (currentEditingUserId == null) {
            interactedFields.add(UserManagementViewModel.FormField.PASSWORD);
        }

        validateUserForm();
        if (Boolean.FALSE.equals(isFormValid.getValue()) || selectedRole.getValue() == null) return;

        _userFormState.setValue(new UserFormUIState.Submitting());

        String usernameString = username.getValue();
        String nameString = name.getValue();
        String surnameString = surname.getValue();
        String emailString = email.getValue();
        String passwordString = password.getValue();
        String role = selectedRole.getValue().getType().toString();

        if (currentEditingUserId == null) {
            AddUserRequest request = new AddUserRequest(
                    usernameString,
                    passwordString,
                    emailString,
                    nameString,
                    surnameString,
                    role
            );
            createUser(request);
        } else {
            UpdateUserRequest request = new UpdateUserRequest(
                    currentEditingUserId,
                    usernameString,
                    emailString,
                    nameString,
                    surnameString,
                    role
            );
            updateUser(request);
        }
    }
}
