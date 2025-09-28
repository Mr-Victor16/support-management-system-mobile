package com.example.support_management_system_mobile.ui.ticket;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.utils.AuthContext;
import com.example.support_management_system_mobile.data.repository.CategoryRepository;
import com.example.support_management_system_mobile.data.repository.PriorityRepository;
import com.example.support_management_system_mobile.data.repository.SoftwareRepository;
import com.example.support_management_system_mobile.data.repository.StatusRepository;
import com.example.support_management_system_mobile.data.repository.TicketRepository;
import com.example.support_management_system_mobile.data.models.Category;
import com.example.support_management_system_mobile.data.models.Event;
import com.example.support_management_system_mobile.data.models.Priority;
import com.example.support_management_system_mobile.data.models.Software;
import com.example.support_management_system_mobile.data.models.Status;
import com.example.support_management_system_mobile.data.models.Ticket;
import com.example.support_management_system_mobile.data.models.TicketReply;
import com.example.support_management_system_mobile.data.payload.request.add.AddTicketRequest;
import com.example.support_management_system_mobile.data.payload.request.update.UpdateTicketRequest;
import com.example.support_management_system_mobile.ui.ticket.details.TicketDetailsUIState;
import com.example.support_management_system_mobile.ui.ticket.form.TicketFormUIState;
import com.example.support_management_system_mobile.ui.ticket.list.TicketListUIState;
import com.example.support_management_system_mobile.utils.FilePreparer;
import com.example.support_management_system_mobile.utils.validators.TicketValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class TicketViewModel extends ViewModel {
    public enum DetailsNavigation { GO_BACK, GO_TO_EDIT, GO_TO_IMAGES }
    public enum FormField { TITLE, DESCRIPTION, VERSION }

    private final TicketRepository ticketRepository;
    private final StatusRepository statusRepository;
    private final PriorityRepository priorityRepository;
    private final SoftwareRepository softwareRepository;
    private final CategoryRepository categoryRepository;
    private final Application application;
    private final AuthContext authContext;

    private final FilePreparer filePreparer;

    private final MutableLiveData<TicketListUIState> _ticketListState = new MutableLiveData<>();
    public final LiveData<TicketListUIState> ticketListState = _ticketListState;

    private final MutableLiveData<TicketDetailsUIState> _ticketDetailsState = new MutableLiveData<>();
    public final LiveData<TicketDetailsUIState> ticketDetailsState = _ticketDetailsState;

    public final MutableLiveData<String> replyContent = new MutableLiveData<>("");
    public final LiveData<Boolean> isReplyValid = Transformations.map(replyContent, TicketValidator::isTicketReplyValid);

    private final MutableLiveData<Event<List<Status>>> _statusesEvent = new MutableLiveData<>();
    public LiveData<Event<List<Status>>> getStatusesEvent() { return _statusesEvent; }

    private final MutableLiveData<Event<String>> _toastMessage = new MutableLiveData<>();
    public final LiveData<Event<String>> toastMessage = _toastMessage;

    private final MutableLiveData<Event<DetailsNavigation>> _detailsNavigation = new MutableLiveData<>();
    public final LiveData<Event<DetailsNavigation>> detailsNavigation = _detailsNavigation;

    private final MutableLiveData<Event<Boolean>> _pickImageEvent = new MutableLiveData<>();
    public LiveData<Event<Boolean>> getPickImageEvent() { return _pickImageEvent; }

    private final MutableLiveData<List<Category>> _categoryList = new MutableLiveData<>();
    public final LiveData<List<Category>> categoryList = _categoryList;
    private final MutableLiveData<List<Priority>> _priorityList = new MutableLiveData<>();
    public final LiveData<List<Priority>> priorityList = _priorityList;
    private final MutableLiveData<List<Software>> _softwareList = new MutableLiveData<>();
    public final LiveData<List<Software>> softwareList = _softwareList;

    public final MutableLiveData<String> title = new MutableLiveData<>("");
    public final MutableLiveData<String> description = new MutableLiveData<>("");
    public final MutableLiveData<String> version = new MutableLiveData<>("");
    public final MutableLiveData<Category> selectedCategory = new MutableLiveData<>();
    public final MutableLiveData<Priority> selectedPriority = new MutableLiveData<>();
    public final MutableLiveData<Software> selectedSoftware = new MutableLiveData<>();

    private final MutableLiveData<TicketFormUIState> _ticketFormState = new MutableLiveData<>();
    public final LiveData<TicketFormUIState> ticketFormState = _ticketFormState;

    private final MediatorLiveData<Boolean> _isFormValid = new MediatorLiveData<>();
    public final LiveData<Boolean> isFormValid = _isFormValid;

    private final MutableLiveData<Integer> _titleError = new MutableLiveData<>(null);
    public final LiveData<Integer> titleError = _titleError;
    private final MutableLiveData<Integer> _descriptionError = new MutableLiveData<>(null);
    public final LiveData<Integer> descriptionError = _descriptionError;
    private final MutableLiveData<Integer> _versionError = new MutableLiveData<>(null);
    public final LiveData<Integer> versionError = _versionError;

    private final Set<FormField> interactedFields = new HashSet<>();

    private Long currentEditingTicketId = null;

    @Inject
    public TicketViewModel(Application application, TicketRepository ticketRepository, StatusRepository statusRepository,
                           PriorityRepository priorityRepository, SoftwareRepository softwareRepository,
                           CategoryRepository categoryRepository, AuthContext authContext, FilePreparer filePreparer) {
        this.application = application;
        this.ticketRepository = ticketRepository;
        this.statusRepository = statusRepository;
        this.priorityRepository = priorityRepository;
        this.softwareRepository = softwareRepository;
        this.categoryRepository = categoryRepository;
        this.authContext = authContext;
        this.filePreparer = filePreparer;

        _isFormValid.addSource(title, value -> validateTicketForm());
        _isFormValid.addSource(description, value -> validateTicketForm());
        _isFormValid.addSource(version, value -> validateTicketForm());
        _isFormValid.addSource(selectedCategory, value -> validateTicketForm());
        _isFormValid.addSource(selectedPriority, value -> validateTicketForm());
        _isFormValid.addSource(selectedSoftware, value -> validateTicketForm());
    }

    public void loadTickets() {
        if (authContext.isAdmin()) {
            final int headerRes = R.string.tickets_header;
            String errorMessageToast = application.getString(R.string.admin_no_access_tickets);
            String errorMessage = application.getString(R.string.no_data_to_display);

            _ticketListState.setValue(new TicketListUIState.AccessDenied(errorMessage, headerRes));
            _toastMessage.setValue(new Event<>(errorMessageToast));
            return;
        }

        boolean isUserRole = authContext.isUser();
        final int headerRes = isUserRole ? R.string.my_tickets_header : R.string.all_tickets_header;
        _ticketListState.setValue(new TicketListUIState.Loading(headerRes));

        Callback<List<Ticket>> callback = new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Ticket>> call, Response<List<Ticket>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _ticketListState.postValue(new TicketListUIState.Success(response.body(), isUserRole, headerRes));
                } else {
                    String errorMessage = application.getString(R.string.no_data_to_display);
                    _ticketListState.postValue(new TicketListUIState.Error(errorMessage, headerRes));
                    _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Ticket>> call, @NonNull Throwable t) {
                String errorMessage = application.getString(R.string.no_data_to_display);
                _ticketListState.postValue(new TicketListUIState.Error(errorMessage, headerRes));
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        };

        if (isUserRole) {
            ticketRepository.getUserTickets(callback);
        } else {
            ticketRepository.getAllTickets(callback);
        }
    }

    public void loadTicketDetails(Long ticketId) {
        if (ticketId == null || ticketId <= 0) {
            _ticketDetailsState.setValue(new TicketDetailsUIState.Error(R.string.ticket_not_found));
            return;
        }
        _ticketDetailsState.setValue(new TicketDetailsUIState.Loading());
        ticketRepository.getTicketById(ticketId, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Ticket> call, @NonNull Response<Ticket> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _ticketDetailsState.postValue(new TicketDetailsUIState.Success(response.body(), authContext.getCurrentUser()));
                } else {
                    _ticketDetailsState.postValue(new TicketDetailsUIState.Error(R.string.ticket_not_found));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Ticket> call, @NonNull Throwable t) {
                _ticketDetailsState.postValue(new TicketDetailsUIState.Error(R.string.server_error));
            }
        });
    }

    public void addReply() {
        if (Boolean.FALSE.equals(isReplyValid.getValue())) return;
        Ticket ticket = getCurrentTicket();
        if (ticket == null) return;

        ticketRepository.addReply(ticket.id(), replyContent.getValue(), new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    loadTicketDetails(ticket.id());
                    replyContent.postValue("");
                    _toastMessage.postValue(new Event<>(application.getString(R.string.reply_added)));
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.reply_add_failed)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public void loadStatuses() {
        statusRepository.getStatuses(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Status>> call, @NonNull Response<List<Status>> response) {
                if (response.isSuccessful()) {
                    _statusesEvent.postValue(new Event<>(response.body()));
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.status_load_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Status>> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public void changeStatus(Status newStatus) {
        Ticket ticket = getCurrentTicket();
        if (ticket == null) return;

        ticketRepository.changeStatus(ticket.id(), newStatus.id(), new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    loadTicketDetails(ticket.id());
                    _toastMessage.postValue(new Event<>(application.getString(R.string.status_changed)));
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.status_change_failed)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public void deleteTicket() {
        Ticket ticket = getCurrentTicket();
        if (ticket == null) return;

        ticketRepository.deleteTicket(ticket.id(), new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.ticket_deleted)));
                    _detailsNavigation.postValue(new Event<>(DetailsNavigation.GO_BACK));
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.ticket_delete_failed)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public void deleteReply(TicketReply reply) {
        Ticket ticket = getCurrentTicket();
        if (ticket == null) return;

        ticketRepository.deleteReply(reply.id(), new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    loadTicketDetails(ticket.id());
                    _toastMessage.postValue(new Event<>(application.getString(R.string.reply_deleted)));
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.reply_delete_failed)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public void addImageToTicket(Uri imageUri) {
        Ticket ticket = getCurrentTicket();
        if (ticket == null) {
            _toastMessage.setValue(new Event<>(application.getString(R.string.ticket_not_found)));
            return;
        }

        MultipartBody.Part body = filePreparer.prepareImagePart("files", imageUri);
        if (body == null) {
            _toastMessage.postValue(new Event<>(application.getString(R.string.image_prepare_failed)));
            return;
        }

        ticketRepository.uploadImage(ticket.id(), body, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.image_added)));
                    loadTicketDetails(ticket.id());
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.add_image_failed)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public void deleteImage(Long imageId) {
        Ticket ticket = getCurrentTicket();
        if (ticket == null) return;

        ticketRepository.deleteImage(imageId, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.image_deleted)));
                    loadTicketDetails(ticket.id());
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.image_delete_failed)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public void onEditTicketClicked() {
        _detailsNavigation.setValue(new Event<>(DetailsNavigation.GO_TO_EDIT));
    }

    public void onManageImagesClicked() {
        _detailsNavigation.setValue(new Event<>(DetailsNavigation.GO_TO_IMAGES));
    }

    public void onAddImageClicked() {
        _pickImageEvent.setValue(new Event<>(true));
    }

    public Ticket getCurrentTicket() {
        TicketDetailsUIState currentState = _ticketDetailsState.getValue();
        if (currentState instanceof TicketDetailsUIState.Success) {
            return ((TicketDetailsUIState.Success) currentState).ticket;
        }

        return null;
    }

    public void loadTicketForm(Long ticketId) {
        this.currentEditingTicketId = ticketId;

        _isFormValid.setValue(false);
        interactedFields.clear();

        _ticketFormState.setValue(new TicketFormUIState.Loading());

        final int TOTAL_REQUESTS = 3;
        AtomicInteger successfulRequests = new AtomicInteger(0);
        AtomicInteger failedRequests = new AtomicInteger(0);

        Runnable checkCompletion = () -> {
            if (successfulRequests.incrementAndGet() == TOTAL_REQUESTS) {
                if (ticketId != null) {
                    loadTicketForEditing(ticketId);
                } else {
                    prepareNewTicketForm();
                }
            }
        };

        Runnable handleFailure = () -> {
            if (failedRequests.incrementAndGet() == 1) {
                _ticketFormState.postValue(new TicketFormUIState.Error(application.getString(R.string.form_data_load_error)));
            }
        };

        Runnable handleServerError = () -> {
            if (failedRequests.incrementAndGet() == 1) {
                _ticketFormState.postValue(new TicketFormUIState.Error(application.getString(R.string.server_error)));
            }
        };

        categoryRepository.getCategories(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _categoryList.postValue(response.body());
                    checkCompletion.run();
                } else {
                    handleFailure.run();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                handleServerError.run();
            }
        });

        priorityRepository.getPriorities(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Priority>> call, @NonNull Response<List<Priority>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _priorityList.postValue(response.body());
                    checkCompletion.run();
                } else {
                    handleFailure.run();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Priority>> call, @NonNull Throwable t) {
                handleServerError.run();
            }
        });

        softwareRepository.getSoftwareList(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Software>> call, @NonNull Response<List<Software>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _softwareList.postValue(response.body());
                    checkCompletion.run();
                } else {
                    handleFailure.run();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Software>> call, @NonNull Throwable t) {
                handleServerError.run();
            }
        });
    }

    private void loadTicketForEditing(Long ticketId) {
        ticketRepository.getTicketById(ticketId, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Ticket> call, @NonNull Response<Ticket> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Ticket item = response.body();
                    title.postValue(item.title());
                    description.postValue(item.description());
                    version.postValue(item.version());
                    selectedCategory.postValue(item.category());
                    selectedPriority.postValue(item.priority());
                    selectedSoftware.postValue(item.software());
                    _ticketFormState.postValue(new TicketFormUIState.Editing(R.string.save_changes_button));
                } else {
                    _ticketFormState.postValue(new TicketFormUIState.Error(application.getString(R.string.error_loading_data)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Ticket> call, @NonNull Throwable t) {
                _ticketFormState.postValue(new TicketFormUIState.Error(application.getString(R.string.server_error)));
            }
        });
    }

    private void validateTicketForm() {
        boolean isTitleValid = TicketValidator.isTicketTitleValid(title.getValue());
        boolean isDescriptionValid = TicketValidator.isTicketDescriptionValid(description.getValue());
        boolean isVersionValid = TicketValidator.isTicketVersionValid(version.getValue());
        boolean areDropdownsSelected = selectedCategory.getValue() != null && selectedPriority.getValue() != null && selectedSoftware.getValue() != null;

        _titleError.setValue(interactedFields.contains(FormField.TITLE) && !isTitleValid ? R.string.ticket_title_error : null);
        _descriptionError.setValue(interactedFields.contains(FormField.DESCRIPTION) && !isDescriptionValid ? R.string.ticket_description_error : null);
        _versionError.setValue(interactedFields.contains(FormField.VERSION) && !isVersionValid ? R.string.software_version_error : null);

        _isFormValid.setValue(isTitleValid && isDescriptionValid && isVersionValid && areDropdownsSelected);
    }

    public void saveTicket() {
        interactedFields.add(FormField.TITLE);
        interactedFields.add(FormField.DESCRIPTION);
        interactedFields.add(FormField.VERSION);
        validateTicketForm();

        if (Boolean.FALSE.equals(_isFormValid.getValue())) return;

        _ticketFormState.setValue(new TicketFormUIState.Submitting());

        Long categoryId = selectedCategory.getValue() != null ? selectedCategory.getValue().id() : null;
        Long priorityId = selectedPriority.getValue() != null ? selectedPriority.getValue().id() : null;
        Long softwareId = selectedSoftware.getValue() != null ? selectedSoftware.getValue().id() : null;

        if (currentEditingTicketId == null) {
            AddTicketRequest request = new AddTicketRequest(
                    title.getValue(), description.getValue(), categoryId,
                    priorityId, version.getValue(), softwareId);
            createTicket(request);
        } else {
            UpdateTicketRequest request = new UpdateTicketRequest(
                    currentEditingTicketId, title.getValue(), description.getValue(),
                    categoryId, priorityId, version.getValue(), softwareId);
            updateTicket(request);
        }
    }

    private void createTicket(AddTicketRequest request) {
        ticketRepository.createTicket(request, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.ticket_added)));
                    _ticketFormState.postValue(new TicketFormUIState.Success());
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.add_ticket_error)));

                    if (_ticketFormState.getValue() instanceof TicketFormUIState.Submitting) {
                        _ticketFormState.postValue(new TicketFormUIState.Editing(R.string.add_ticket_button));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
                if (_ticketFormState.getValue() instanceof TicketFormUIState.Submitting) {
                    _ticketFormState.postValue(new TicketFormUIState.Editing(R.string.add_ticket_button));
                }
            }
        });
    }

    private void updateTicket(UpdateTicketRequest request) {
        ticketRepository.updateTicket(request, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.ticket_updated)));
                    _ticketFormState.postValue(new TicketFormUIState.Success());
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.update_ticket_error)));

                    if (_ticketFormState.getValue() instanceof TicketFormUIState.Submitting) {
                        _ticketFormState.postValue(new TicketFormUIState.Editing(R.string.save_changes_button));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));

                if (_ticketFormState.getValue() instanceof TicketFormUIState.Submitting) {
                    _ticketFormState.postValue(new TicketFormUIState.Editing(R.string.save_changes_button));
                }
            }
        });
    }

    public void onFieldChanged(FormField field, String value) {
        interactedFields.add(field);

        switch (field) {
            case TITLE:
                if (!java.util.Objects.equals(title.getValue(), value)) title.setValue(value);
                break;
            case DESCRIPTION:
                if (!java.util.Objects.equals(description.getValue(), value)) description.setValue(value);
                break;
            case VERSION:
                if (!java.util.Objects.equals(version.getValue(), value)) version.setValue(value);
                break;
        }
    }

    private void prepareNewTicketForm() {
        title.postValue("");
        description.postValue("");
        version.postValue("");
        selectedCategory.postValue(null);
        selectedPriority.postValue(null);
        selectedSoftware.postValue(null);

        _ticketFormState.postValue(new TicketFormUIState.Editing(R.string.add_ticket_button));
        validateTicketForm();
    }
}
