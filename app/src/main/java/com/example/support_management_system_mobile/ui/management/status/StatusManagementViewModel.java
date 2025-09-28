package com.example.support_management_system_mobile.ui.management.status;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.utils.AuthContext;
import com.example.support_management_system_mobile.data.repository.StatusRepository;
import com.example.support_management_system_mobile.data.models.Event;
import com.example.support_management_system_mobile.data.payload.request.add.AddStatusRequest;
import com.example.support_management_system_mobile.data.payload.request.update.UpdateStatusRequest;
import com.example.support_management_system_mobile.data.payload.response.StatusResponse;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class StatusManagementViewModel extends ViewModel {
    private final StatusRepository statusRepository;
    private final Application application;
    private final AuthContext authContext;

    private final MutableLiveData<StatusListUIState> _statusListState = new MutableLiveData<>();
    public final LiveData<StatusListUIState> statusListState = _statusListState;

    private final MutableLiveData<Event<String>> _toastMessage = new MutableLiveData<>();
    public final LiveData<Event<String>> toastMessage = _toastMessage;

    @Inject
    public StatusManagementViewModel(Application application, StatusRepository statusRepository, AuthContext authContext) {
        this.application = application;
        this.statusRepository = statusRepository;
        this.authContext = authContext;
    }

    public void loadStatusList() {
        _statusListState.setValue(new StatusListUIState.Loading());

        statusRepository.getStatusesWithUseNumber(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<StatusResponse>> call, @NonNull Response<List<StatusResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean canManage = authContext.isAdmin();
                    _statusListState.postValue(new StatusListUIState.Success(response.body(), canManage));
                } else {
                    _statusListState.postValue(new StatusListUIState.Error(application.getString(R.string.error_loading_data)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<StatusResponse>> call, @NonNull Throwable t) {
                _statusListState.postValue(new StatusListUIState.Error(application.getString(R.string.server_error)));
            }
        });
    }

    public void deleteStatus(long statusId) {
        statusRepository.deleteStatus(statusId, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.status_deleted_successfully)));
                    loadStatusList();
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.status_delete_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public void createStatus(String name, boolean closeTicket, boolean defaultStatus) {
        statusRepository.createStatus(new AddStatusRequest(name, closeTicket, defaultStatus), new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.status_added_successfully)));
                    loadStatusList();
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.status_add_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public void updateStatus(Long statusId, String newName, boolean closeTicket, boolean defaultStatus) {
        statusRepository.updateStatus(new UpdateStatusRequest(statusId, newName, closeTicket, defaultStatus), new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.status_updated_successfully)));
                    loadStatusList();
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.status_update_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }
}
