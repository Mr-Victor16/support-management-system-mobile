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
import com.example.support_management_system_mobile.auth.AuthContext;
import com.example.support_management_system_mobile.data.repository.CategoryRepository;
import com.example.support_management_system_mobile.data.repository.PriorityRepository;
import com.example.support_management_system_mobile.data.repository.SoftwareRepository;
import com.example.support_management_system_mobile.data.repository.StatusRepository;
import com.example.support_management_system_mobile.data.repository.TicketRepository;
import com.example.support_management_system_mobile.models.Category;
import com.example.support_management_system_mobile.models.Event;
import com.example.support_management_system_mobile.models.Priority;
import com.example.support_management_system_mobile.models.Software;
import com.example.support_management_system_mobile.models.Status;
import com.example.support_management_system_mobile.models.Ticket;
import com.example.support_management_system_mobile.models.TicketReply;
import com.example.support_management_system_mobile.payload.request.add.AddTicketRequest;
import com.example.support_management_system_mobile.payload.request.update.UpdateTicketRequest;
import com.example.support_management_system_mobile.ui.ticket.details.TicketDetailsUIState;
import com.example.support_management_system_mobile.ui.ticket.form.TicketFormUIState;
import com.example.support_management_system_mobile.ui.ticket.form.TicketFormValidationState;
import com.example.support_management_system_mobile.ui.ticket.list.TicketListUIState;
import com.example.support_management_system_mobile.validators.TicketValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
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

    private final MutableLiveData<List<Category>> _categories = new MutableLiveData<>();
    public final LiveData<List<Category>> categories = _categories;

    private final MutableLiveData<List<Priority>> _priorities = new MutableLiveData<>();
    public final LiveData<List<Priority>> priorities = _priorities;

    private final MutableLiveData<List<Software>> _software = new MutableLiveData<>();
    public final LiveData<List<Software>> software = _software;

    public final MutableLiveData<String> ticketTitle = new MutableLiveData<>("");
    public final MutableLiveData<String> ticketDescription = new MutableLiveData<>("");
    public final MutableLiveData<String> ticketVersion = new MutableLiveData<>("");

    public final MutableLiveData<Category> selectedCategory = new MutableLiveData<>();
    public final MutableLiveData<Priority> selectedPriority = new MutableLiveData<>();
    public final MutableLiveData<Software> selectedSoftware = new MutableLiveData<>();

    private final MutableLiveData<TicketFormUIState> _formState = new MutableLiveData<>();
    public final LiveData<TicketFormUIState> formState = _formState;

    private final MediatorLiveData<Boolean> _isFormValid = new MediatorLiveData<>();
    public final LiveData<Boolean> isFormValid = _isFormValid;

    private final MediatorLiveData<TicketFormValidationState> _validationState = new MediatorLiveData<>();
    public final LiveData<TicketFormValidationState> validationState = _validationState;

    private final Set<FormField> touchedFields = new HashSet<>();

    private Long currentEditingTicketId = null;

    @Inject
    public TicketViewModel(Application application, TicketRepository ticketRepository, StatusRepository statusRepository,
                           PriorityRepository priorityRepository, SoftwareRepository softwareRepository,
                           CategoryRepository categoryRepository, AuthContext authContext) {
        this.application = application;
        this.ticketRepository = ticketRepository;
        this.statusRepository = statusRepository;
        this.priorityRepository = priorityRepository;
        this.softwareRepository = softwareRepository;
        this.categoryRepository = categoryRepository;
        this.authContext = authContext;

        _validationState.addSource(ticketTitle, value -> validateForm());
        _validationState.addSource(ticketDescription, value -> validateForm());
        _validationState.addSource(ticketVersion, value -> validateForm());
        _validationState.addSource(selectedCategory, value -> validateForm());
        _validationState.addSource(selectedPriority, value -> validateForm());
        _validationState.addSource(selectedSoftware, value -> validateForm());
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
        ticketRepository.getTicketById(ticketId, new Callback<Ticket>() {
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

        ticketRepository.addReply(ticket.getId(), replyContent.getValue(), new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    loadTicketDetails(ticket.getId());
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
        statusRepository.getStatuses(new Callback<List<Status>>() {
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

        ticketRepository.changeStatus(ticket.getId(), newStatus.getId(), new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.isSuccessful()) {
                    loadTicketDetails(ticket.getId());
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

        ticketRepository.deleteTicket(ticket.getId(), new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.isSuccessful()) {
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

        ticketRepository.deleteReply(reply.getId(), new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    loadTicketDetails(ticket.getId());
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

        ticketRepository.uploadImage(ticket.getId(), imageUri, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.image_added)));
                    loadTicketDetails(ticket.getId());
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

        ticketRepository.deleteImage(imageId, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.image_deleted)));
                    loadTicketDetails(ticket.getId());
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

    public void loadForm(Long ticketId) {
        this.currentEditingTicketId = ticketId;
        _formState.setValue(new TicketFormUIState.Loading());
        touchedFields.clear();

        categoryRepository.getCategories(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    _categories.postValue(response.body());
                    loadPriorities(ticketId);
                } else {
                    handleFormDataLoadError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                handleFormDataLoadError();
            }
        });
    }

    private void loadPriorities(Long ticketId) {
        priorityRepository.getPriorities(new Callback<List<Priority>>() {
            @Override
            public void onResponse(@NonNull Call<List<Priority>> call, @NonNull Response<List<Priority>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    _priorities.postValue(response.body());
                    loadSoftware(ticketId);
                } else {
                    handleFormDataLoadError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Priority>> call, @NonNull Throwable t) {
                handleFormDataLoadError();
            }
        });
    }

    private void loadSoftware(Long ticketId) {
        softwareRepository.getSoftwareList(new Callback<List<Software>>() {
            @Override
            public void onResponse(@NonNull Call<List<Software>> call, @NonNull Response<List<Software>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    _software.postValue(response.body());

                    if (ticketId != null) {
                        loadTicketForEditing(ticketId);
                    } else {
                        prepareNewTicketForm();
                    }
                } else {
                    handleFormDataLoadError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Software>> call, @NonNull Throwable t) {
                handleFormDataLoadError();
            }
        });
    }

    private void loadTicketForEditing(Long ticketId) {
        ticketRepository.getTicketById(ticketId, new Callback<Ticket>() {
            @Override
            public void onResponse(@NonNull Call<Ticket> call, @NonNull Response<Ticket> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Ticket ticket = response.body();
                    ticketTitle.postValue(ticket.getTitle());
                    ticketDescription.postValue(ticket.getDescription());
                    ticketVersion.postValue(ticket.getVersion());

                    selectedCategory.postValue(ticket.getCategory());
                    selectedPriority.postValue(ticket.getPriority());
                    selectedSoftware.postValue(ticket.getSoftware());
                    _formState.postValue(new TicketFormUIState.Editing(R.string.edit_ticket, R.string.save_changes_button));
                } else {
                    _formState.postValue(new TicketFormUIState.Error(R.string.ticket_not_found));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Ticket> call, @NonNull Throwable t) {
                _formState.postValue(new TicketFormUIState.Error(R.string.server_error));
            }
        });
    }

    private void validateForm() {
        Integer titleError = !TicketValidator.isTicketTitleValid(ticketTitle.getValue()) ? R.string.ticket_title_error : null;
        Integer descriptionError = !TicketValidator.isTicketDescriptionValid(ticketDescription.getValue()) ? R.string.ticket_description_error : null;
        Integer versionError = !TicketValidator.isTicketVersionValid(ticketVersion.getValue()) ? R.string.software_version_error : null;

        Integer finalTitleError = touchedFields.contains(FormField.TITLE) ? titleError : null;
        Integer finalDescriptionError = touchedFields.contains(FormField.DESCRIPTION) ? descriptionError : null;
        Integer finalVersionError = touchedFields.contains(FormField.VERSION) ? versionError : null;

        boolean areSpinnersSelected = selectedCategory.getValue() != null &&
                selectedPriority.getValue() != null &&
                selectedSoftware.getValue() != null;

        boolean isSaveEnabled = titleError == null && descriptionError == null && versionError == null && areSpinnersSelected;

        _validationState.setValue(new TicketFormValidationState(finalTitleError, finalDescriptionError, finalVersionError, isSaveEnabled));
    }

    public void saveTicket() {
        if (Boolean.FALSE.equals(isFormValid.getValue())) return;
        _formState.setValue(new TicketFormUIState.Submitting());

        Category category = selectedCategory.getValue();
        Long categoryId = (category != null) ? category.getId() : null;

        Priority priority = selectedPriority.getValue();
        Long priorityId = (priority != null) ? priority.getId() : null;

        Software software = selectedSoftware.getValue();
        Long softwareId = (software != null) ? software.getId() : null;

        if (currentEditingTicketId == null) {
            AddTicketRequest request = new AddTicketRequest(
                    ticketTitle.getValue(),
                    ticketDescription.getValue(),
                    categoryId,
                    priorityId,
                    ticketVersion.getValue(),
                    softwareId
            );
            ticketRepository.createTicket(request, new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    handleSaveResponse(true);
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    handleSaveFailure(true);
                }
            });
        } else {
            UpdateTicketRequest request = new UpdateTicketRequest(
                    currentEditingTicketId,
                    ticketTitle.getValue(),
                    ticketDescription.getValue(),
                    categoryId,
                    priorityId,
                    ticketVersion.getValue(),
                    softwareId
            );
            ticketRepository.updateTicket(request, new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    handleSaveResponse(false);
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    handleSaveFailure(false);
                }
            });
        }
    }

    private void handleSaveResponse(boolean isNewTicket) {
        _formState.postValue(new TicketFormUIState.Success());
        _toastMessage.postValue(new Event<>(isNewTicket ? application.getString(R.string.ticket_added) : application.getString(R.string.ticket_updated)));

        if (isNewTicket) {
            loadTickets();
        } else {
            loadTicketDetails(currentEditingTicketId);
        }
    }

    private void handleSaveFailure(boolean isNewTicket) {
        _formState.postValue(new TicketFormUIState.Error(isNewTicket ? R.string.add_ticket_error : R.string.update_ticket_error));
    }

    public void onFieldTouched(FormField field) {
        if (touchedFields.add(field)) {
            validateForm();
        }
    }

    private void prepareNewTicketForm() {
        ticketTitle.postValue("");
        ticketDescription.postValue("");
        ticketVersion.postValue("");
        selectedCategory.postValue(null);
        selectedPriority.postValue(null);
        selectedSoftware.postValue(null);
        _formState.postValue(new TicketFormUIState.Editing(R.string.new_ticket, R.string.add_ticket_button));
        validateForm();
    }

    private void handleFormDataLoadError() {
        _formState.postValue(new TicketFormUIState.Error(R.string.form_data_load_error));
    }
}
