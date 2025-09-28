package com.example.support_management_system_mobile.ui.management.priority;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.utils.AuthContext;
import com.example.support_management_system_mobile.data.repository.PriorityRepository;
import com.example.support_management_system_mobile.data.models.Event;
import com.example.support_management_system_mobile.data.payload.response.PriorityResponse;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class PriorityManagementViewModel extends ViewModel {
    private final PriorityRepository priorityRepository;
    private final Application application;
    private final AuthContext authContext;

    private final MutableLiveData<Event<String>> _toastMessage = new MutableLiveData<>();
    public final LiveData<Event<String>> toastMessage = _toastMessage;

    private final MutableLiveData<PriorityListUIState> _priorityListState = new MutableLiveData<>();
    public final LiveData<PriorityListUIState> priorityListState = _priorityListState;

    @Inject
    public PriorityManagementViewModel(Application application, PriorityRepository priorityRepository, AuthContext authContext) {
        this.application = application;
        this.priorityRepository = priorityRepository;
        this.authContext = authContext;
    }

    public void loadPriorities() {
        _priorityListState.setValue(new PriorityListUIState.Loading());
        priorityRepository.getPrioritiesWithUseNumber(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<PriorityResponse>> call, @NonNull Response<List<PriorityResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _priorityListState.postValue(new PriorityListUIState.Success(response.body()));
                } else {
                    _priorityListState.postValue(new PriorityListUIState.Error(application.getString(R.string.error_loading_data)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PriorityResponse>> call, @NonNull Throwable t) {
                _priorityListState.postValue(new PriorityListUIState.Error(application.getString(R.string.server_error)));
            }
        });
    }

    public void deletePriority(Long priorityId) {
        priorityRepository.deletePriority(priorityId, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.priority_deleted_successfully)));
                    loadPriorities();
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.priority_delete_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public void createPriority(String name) {
        priorityRepository.createPriority(name, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.priority_added_successfully)));
                    loadPriorities();
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.priority_add_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public void updatePriority(Long priorityId, String newName) {
        priorityRepository.updatePriority(priorityId, newName, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.priority_updated_successfully)));
                    loadPriorities();
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.priority_update_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public AuthContext getAuthContext() {
        return authContext;
    }
}
