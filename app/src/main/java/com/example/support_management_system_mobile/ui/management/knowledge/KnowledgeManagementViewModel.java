package com.example.support_management_system_mobile.ui.management.knowledge;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.utils.AuthContext;
import com.example.support_management_system_mobile.data.repository.KnowledgeRepository;
import com.example.support_management_system_mobile.data.repository.SoftwareRepository;
import com.example.support_management_system_mobile.data.models.Event;
import com.example.support_management_system_mobile.data.models.Knowledge;
import com.example.support_management_system_mobile.data.models.Software;
import com.example.support_management_system_mobile.data.payload.request.add.AddKnowledgeRequest;
import com.example.support_management_system_mobile.data.payload.request.update.UpdateKnowledgeRequest;
import com.example.support_management_system_mobile.utils.validators.KnowledgeValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class KnowledgeManagementViewModel extends ViewModel {
    private final KnowledgeRepository knowledgeRepository;
    private final SoftwareRepository softwareRepository;
    private final Application application;
    private Long currentEditingKnowledgeId = null;
    private final AuthContext authContext;

    public enum FormField { TITLE, CONTENT }
    private final Set<FormField> interactedFields = new HashSet<>();

    private final MutableLiveData<KnowledgeListUIState> _knowledgeListState = new MutableLiveData<>();
    public final LiveData<KnowledgeListUIState> knowledgeListState = _knowledgeListState;

    private final MutableLiveData<KnowledgeFormUIState> _knowledgeFormState = new MutableLiveData<>();
    public final LiveData<KnowledgeFormUIState> knowledgeFormState = _knowledgeFormState;

    public final MutableLiveData<String> knowledgeTitle = new MutableLiveData<>("");
    public final MutableLiveData<String> knowledgeContent = new MutableLiveData<>("");
    public final MutableLiveData<Software> selectedSoftware = new MutableLiveData<>();

    private final MutableLiveData<List<Software>> _softwareList = new MutableLiveData<>();
    public final LiveData<List<Software>> softwareList = _softwareList;

    private final MutableLiveData<Integer> _titleError = new MutableLiveData<>(null);
    public final LiveData<Integer> titleError = _titleError;

    private final MutableLiveData<Integer> _contentError = new MutableLiveData<>(null);
    public final LiveData<Integer> contentError = _contentError;

    private final MediatorLiveData<Boolean> _isFormValid = new MediatorLiveData<>();
    public final LiveData<Boolean> isFormValid = _isFormValid;

    private final MutableLiveData<Event<String>> _toastMessage = new MutableLiveData<>();
    public final LiveData<Event<String>> toastMessage = _toastMessage;

    private final MediatorLiveData<Boolean> _isSaveButtonEnabled = new MediatorLiveData<>();
    public final LiveData<Boolean> isSaveButtonEnabled = _isSaveButtonEnabled;

    @Inject
    public KnowledgeManagementViewModel(Application application, KnowledgeRepository knowledgeRepository,
                                        SoftwareRepository softwareRepository, AuthContext authContext) {
        this.application = application;
        this.authContext = authContext;
        this.knowledgeRepository = knowledgeRepository;
        this.softwareRepository = softwareRepository;

        _isFormValid.addSource(knowledgeTitle, value -> validateKnowledgeForm());
        _isFormValid.addSource(knowledgeContent, value -> validateKnowledgeForm());
        _isFormValid.addSource(selectedSoftware, value -> validateKnowledgeForm());

        _isSaveButtonEnabled.addSource(isFormValid, isValid -> updateButtonState());
        _isSaveButtonEnabled.addSource(knowledgeFormState, state -> updateButtonState());
    }

    private void updateButtonState() {
        boolean isValid = Boolean.TRUE.equals(isFormValid.getValue());
        KnowledgeFormUIState state = knowledgeFormState.getValue();
        boolean isLoadingOrSubmitting = state instanceof KnowledgeFormUIState.Loading || state instanceof KnowledgeFormUIState.Submitting;

        _isSaveButtonEnabled.setValue(isValid && !isLoadingOrSubmitting);
    }

    public void loadKnowledgeItems() {
        _knowledgeListState.setValue(new KnowledgeListUIState.Loading());
        knowledgeRepository.getKnowledgeItems(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Knowledge>> call, @NonNull Response<List<Knowledge>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean canManage = authContext.isAdmin();
                    _knowledgeListState.postValue(new KnowledgeListUIState.Success(response.body(), canManage));
                } else {
                    _knowledgeListState.postValue(new KnowledgeListUIState.Error(application.getString(R.string.error_loading_data)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Knowledge>> call, @NonNull Throwable t) {
                _knowledgeListState.postValue(new KnowledgeListUIState.Error(application.getString(R.string.server_error)));
            }
        });
    }

    public void deleteKnowledgeItem(Long knowledgeId) {
        knowledgeRepository.deleteKnowledgeItem(knowledgeId, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.knowledge_deleted_successfully)));
                    loadKnowledgeItems();
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.knowledge_delete_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public void loadKnowledgeForm(Long knowledgeId) {
        this.currentEditingKnowledgeId = knowledgeId;
        _knowledgeFormState.setValue(new KnowledgeFormUIState.Loading());
        interactedFields.clear();

        softwareRepository.getSoftwareList(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Software>> call, @NonNull Response<List<Software>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    _softwareList.postValue(response.body());
                    if (knowledgeId != null) {
                        loadKnowledgeItemForEditing(knowledgeId);
                    } else {
                        prepareNewKnowledgeForm();
                    }
                } else {
                    _knowledgeFormState.postValue(new KnowledgeFormUIState.Error(application.getString(R.string.form_data_load_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Software>> call, @NonNull Throwable t) {
                _knowledgeFormState.postValue(new KnowledgeFormUIState.Error(application.getString(R.string.server_error)));
            }
        });
    }

    private void loadKnowledgeItemForEditing(Long knowledgeId) {
        knowledgeRepository.getKnowledgeItemById(knowledgeId, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Knowledge> call, @NonNull Response<Knowledge> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Knowledge item = response.body();
                    knowledgeTitle.postValue(item.title());
                    knowledgeContent.postValue(item.content());
                    selectedSoftware.postValue(item.software());
                    _knowledgeFormState.postValue(new KnowledgeFormUIState.Editing(R.string.save_changes_button));
                } else {
                    _knowledgeFormState.postValue(new KnowledgeFormUIState.Error(application.getString(R.string.error_loading_data)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Knowledge> call, @NonNull Throwable t) {
                _knowledgeFormState.postValue(new KnowledgeFormUIState.Error(application.getString(R.string.server_error)));
            }
        });
    }

    private void prepareNewKnowledgeForm() {
        knowledgeTitle.postValue("");
        knowledgeContent.postValue("");
        selectedSoftware.postValue(null);
        _knowledgeFormState.postValue(new KnowledgeFormUIState.Editing(R.string.save));
        validateKnowledgeForm();
    }

    private void validateKnowledgeForm() {
        boolean isTitleValid = KnowledgeValidator.isTitleValid(knowledgeTitle.getValue());
        boolean isContentValid = KnowledgeValidator.isContentValid(knowledgeContent.getValue());
        boolean isSoftwareSelected = selectedSoftware.getValue() != null;

        _titleError.setValue(interactedFields.contains(FormField.TITLE) && !isTitleValid ? R.string.knowledge_title_error : null);
        _contentError.setValue(interactedFields.contains(FormField.CONTENT) && !isContentValid ? R.string.knowledge_content_error : null);

        _isFormValid.setValue(isTitleValid && isContentValid && isSoftwareSelected);
    }

    public void saveKnowledgeItem() {
        interactedFields.add(FormField.TITLE);
        interactedFields.add(FormField.CONTENT);
        validateKnowledgeForm();
        if (Boolean.FALSE.equals(isFormValid.getValue()) || selectedSoftware.getValue() == null) return;

        _knowledgeFormState.setValue(new KnowledgeFormUIState.Submitting());

        String title = knowledgeTitle.getValue();
        String content = knowledgeContent.getValue();
        Long softwareId = selectedSoftware.getValue().id();

        Callback<Void> callback = createSaveCallback();
        if (currentEditingKnowledgeId == null) {
            AddKnowledgeRequest request = new AddKnowledgeRequest(softwareId, title, content);
            knowledgeRepository.createKnowledgeItem(request, callback);
        } else {
            UpdateKnowledgeRequest request = new UpdateKnowledgeRequest(currentEditingKnowledgeId, title, content, softwareId);
            knowledgeRepository.updateKnowledgeItem(request, callback);
        }
    }

    private Callback<Void> createSaveCallback() {
        boolean isNewEntry = currentEditingKnowledgeId == null;

        return new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(isNewEntry ? R.string.entry_added_successfully : R.string.entry_updated_successfully)));
                    loadKnowledgeItems();
                    _knowledgeFormState.postValue(new KnowledgeFormUIState.Success());
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(isNewEntry ? R.string.entry_add_error: R.string.entry_update_error)));

                    if (_knowledgeFormState.getValue() instanceof KnowledgeFormUIState.Submitting) {
                        _knowledgeFormState.postValue(new KnowledgeFormUIState.Editing(isNewEntry ? R.string.save : R.string.save_changes_button));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));

                if (_knowledgeFormState.getValue() instanceof KnowledgeFormUIState.Submitting) {
                    _knowledgeFormState.postValue(new KnowledgeFormUIState.Editing(isNewEntry ? R.string.save : R.string.save_changes_button));
                }
            }
        };
    }

    public void onFieldChanged(FormField field, String value) {
        interactedFields.add(field);

        switch (field) {
            case TITLE:
                if (!java.util.Objects.equals(knowledgeTitle.getValue(), value)) {
                    knowledgeTitle.setValue(value);
                }
                break;
            case CONTENT:
                if (!java.util.Objects.equals(knowledgeContent.getValue(), value)) {
                    knowledgeContent.setValue(value);
                }
                break;
        }
    }
}
