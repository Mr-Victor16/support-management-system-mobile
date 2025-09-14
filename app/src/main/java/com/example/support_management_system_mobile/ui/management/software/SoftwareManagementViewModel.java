package com.example.support_management_system_mobile.ui.management.software;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.utils.AuthContext;
import com.example.support_management_system_mobile.data.repository.SoftwareRepository;
import com.example.support_management_system_mobile.data.models.Event;
import com.example.support_management_system_mobile.data.payload.response.SoftwareResponse;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class SoftwareManagementViewModel extends ViewModel {
    private final SoftwareRepository softwareRepository;
    private final Application application;
    private final AuthContext authContext;

    private final MutableLiveData<SoftwareListUIState> _softwareListState = new MutableLiveData<>();
    public final LiveData<SoftwareListUIState> softwareListState = _softwareListState;

    private final MutableLiveData<Event<String>> _toastMessage = new MutableLiveData<>();
    public final LiveData<Event<String>> toastMessage = _toastMessage;

    @Inject
    public SoftwareManagementViewModel(Application application, SoftwareRepository softwareRepository, AuthContext authContext) {
        this.application = application;
        this.softwareRepository = softwareRepository;
        this.authContext = authContext;
    }

    public void loadSoftwareList() {
        _softwareListState.setValue(new SoftwareListUIState.Loading());

        softwareRepository.getSoftwareListWithUseNumber(new Callback<List<SoftwareResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<SoftwareResponse>> call, @NonNull Response<List<SoftwareResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean canManage = authContext.isAdmin();
                    _softwareListState.postValue(new SoftwareListUIState.Success(response.body(), canManage));
                } else {
                    _softwareListState.postValue(new SoftwareListUIState.Error(application.getString(R.string.error_loading_data)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SoftwareResponse>> call, @NonNull Throwable t) {
                _softwareListState.postValue(new SoftwareListUIState.Error(application.getString(R.string.server_error)));
            }
        });
    }

    public void deleteSoftware(long softwareId) {
        softwareRepository.deleteSoftware(softwareId, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.software_deleted_successfully)));
                    loadSoftwareList();
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.software_delete_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public void createSoftware(String name, String description) {
        softwareRepository.createSoftware(name, description, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.software_added_successfully)));
                    loadSoftwareList();
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.software_add_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public void updateSoftware(Long softwareId, String newName, String newDescription) {
        softwareRepository.updateSoftware(softwareId, newName, newDescription, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.software_updated_successfully)));
                    loadSoftwareList();
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.software_update_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

}
