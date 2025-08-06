package com.example.support_management_system_mobile.ui.knowledge;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.data.repository.KnowledgeRepository;
import com.example.support_management_system_mobile.models.Knowledge;
import com.example.support_management_system_mobile.models.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class KnowledgeViewModel extends ViewModel {
    private final KnowledgeRepository knowledgeRepository;
    private final Application application;

    private final MutableLiveData<KnowledgeUIState> _uiState = new MutableLiveData<>();
    public final LiveData<KnowledgeUIState> uiState = _uiState;

    private final MutableLiveData<Event<String>> _toastEvent = new MutableLiveData<>();
    public final LiveData<Event<String>> toastEvent = _toastEvent;

    @Inject
    public KnowledgeViewModel(Application application, KnowledgeRepository knowledgeRepository) {
        this.application = application;
        this.knowledgeRepository = knowledgeRepository;
        loadKnowledge();
    }

    public void loadKnowledge() {
        _uiState.setValue(new KnowledgeUIState.Loading());

        knowledgeRepository.getKnowledgeItems(new Callback<List<Knowledge>>() {
            @Override
            public void onResponse(@NonNull Call<List<Knowledge>> call, @NonNull Response<List<Knowledge>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<KnowledgeUIModel> uiModels = response.body().stream()
                            .map(knowledge -> new KnowledgeUIModel(knowledge, false))
                            .collect(Collectors.toList());
                    _uiState.postValue(new KnowledgeUIState.Success(uiModels));
                } else {
                    _uiState.postValue(new KnowledgeUIState.Empty(application.getString(R.string.no_data_to_display)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Knowledge>> call, @NonNull Throwable t) {
                _toastEvent.postValue(new Event<>(application.getString(R.string.server_error)));
                _uiState.postValue(new KnowledgeUIState.Empty(application.getString(R.string.no_data_to_display)));
            }
        });
    }

    public void onKnowledgeItemClicked(Long knowledgeId) {
        KnowledgeUIState currentState = _uiState.getValue();

        if (!(currentState instanceof KnowledgeUIState.Success)) {
            return;
        }

        List<KnowledgeUIModel> currentList = ((KnowledgeUIState.Success) currentState).items;

        List<KnowledgeUIModel> newList = new ArrayList<>();
        for (KnowledgeUIModel item : currentList) {
            if (item.getKnowledge().getId().equals(knowledgeId)) {
                newList.add(new KnowledgeUIModel(item.getKnowledge(), !item.isExpanded()));
            } else {
                newList.add(new KnowledgeUIModel(item.getKnowledge(), item.isExpanded()));

            }
        }

        _uiState.setValue(new KnowledgeUIState.Success(newList));
    }
}
