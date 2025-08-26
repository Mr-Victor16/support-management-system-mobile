package com.example.support_management_system_mobile.ui.software;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.data.repository.SoftwareRepository;
import com.example.support_management_system_mobile.models.Event;
import com.example.support_management_system_mobile.models.Software;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class SupportedSoftwareViewModel extends ViewModel {
    private final SoftwareRepository softwareRepository;
    private final Application application;

    private final MutableLiveData<SoftwareUIState> _screenState = new MutableLiveData<>();
    public final LiveData<SoftwareUIState> screenState = _screenState;

    private final MutableLiveData<Event<String>> _toastEvent = new MutableLiveData<>();
    public final LiveData<Event<String>> toastEvent = _toastEvent;

    @Inject
    public SupportedSoftwareViewModel(Application application, SoftwareRepository softwareRepository) {
        this.application = application;
        this.softwareRepository = softwareRepository;
        loadSupportedSoftware();
    }

    public void loadSupportedSoftware() {
        _screenState.setValue(new SoftwareUIState.Loading());

        softwareRepository.getSoftwareList(new Callback<List<Software>>() {
            @Override
            public void onResponse(@NonNull Call<List<Software>> call, @NonNull Response<List<Software>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<SoftwareUIModel> uiModels = response.body().stream()
                            .map(software -> new SoftwareUIModel(software, false))
                            .collect(Collectors.toList());
                    _screenState.postValue(new SoftwareUIState.Success(uiModels));
                } else {
                    _screenState.postValue(new SoftwareUIState.Empty(application.getString(R.string.no_data_to_display)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Software>> call, @NonNull Throwable t) {
                _toastEvent.postValue(new Event<>(application.getString(R.string.server_error)));
                _screenState.postValue(new SoftwareUIState.Empty(application.getString(R.string.no_data_to_display)));
            }
        });
    }

    public void onSoftwareItemClicked(SoftwareUIModel clickedItem) {
        if (!(_screenState.getValue() instanceof SoftwareUIState.Success)) {
            return;
        }

        List<SoftwareUIModel> currentList = ((SoftwareUIState.Success) _screenState.getValue()).items;
        List<SoftwareUIModel> newList = new ArrayList<>();

        for (SoftwareUIModel item : currentList) {
            if (item.getSoftware().getId().equals(clickedItem.getSoftware().getId())) {
                newList.add(new SoftwareUIModel(item.getSoftware(), !item.isExpanded()));
            } else {
                newList.add(item);
            }
        }
        _screenState.setValue(new SoftwareUIState.Success(newList));
    }
}
