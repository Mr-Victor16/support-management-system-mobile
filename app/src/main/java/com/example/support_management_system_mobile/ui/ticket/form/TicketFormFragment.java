package com.example.support_management_system_mobile.ui.ticket.form;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.data.models.Category;
import com.example.support_management_system_mobile.data.models.Priority;
import com.example.support_management_system_mobile.data.models.Software;
import com.example.support_management_system_mobile.ui.ticket.TicketViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TicketFormFragment extends Fragment {
    private TicketViewModel viewModel;

    private ProgressBar loadingProgressBar, submittingProgressBar;
    private ScrollView formContent;
    private TextView errorTextView, formHeader;
    private TextInputLayout titleInputLayout, descriptionInputLayout, versionInputLayout;
    private EditText editTitle, editDescription, editVersion;
    private AutoCompleteTextView categoryAutoComplete, priorityAutoComplete, softwareAutoComplete;
    private Button saveButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ticket_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TicketViewModel.class);

        Long ticketId = getArguments() != null ? getArguments().getLong("ticketId", -1L) : -1L;
        if (ticketId == -1L) ticketId = null;

        initViews(view);

        int headerResId = (ticketId == null) ? R.string.new_ticket : R.string.edit_ticket;
        formHeader.setText(headerResId);

        setupInputListeners();
        observeViewModel();

        viewModel.loadTicketForm(ticketId);
    }

    private void initViews(View view) {
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);
        submittingProgressBar = view.findViewById(R.id.submittingProgressBar);
        formContent = view.findViewById(R.id.formContent);
        formHeader = view.findViewById(R.id.formHeader);
        editTitle = view.findViewById(R.id.editTitle);
        editDescription = view.findViewById(R.id.editDescription);
        editVersion = view.findViewById(R.id.editVersion);
        categoryAutoComplete = view.findViewById(R.id.categoryAutoComplete);
        priorityAutoComplete = view.findViewById(R.id.priorityAutoComplete);
        softwareAutoComplete = view.findViewById(R.id.softwareAutoComplete);
        saveButton = view.findViewById(R.id.saveButton);
        titleInputLayout = view.findViewById(R.id.titleInputLayout);
        descriptionInputLayout = view.findViewById(R.id.descriptionInputLayout);
        versionInputLayout = view.findViewById(R.id.versionInputLayout);
        errorTextView = view.findViewById(R.id.errorTextView);
    }

    private void setFormEnabled(boolean isEnabled) {
        editTitle.setEnabled(isEnabled);
        editDescription.setEnabled(isEnabled);
        editVersion.setEnabled(isEnabled);
        categoryAutoComplete.setEnabled(isEnabled);
        priorityAutoComplete.setEnabled(isEnabled);
        softwareAutoComplete.setEnabled(isEnabled);
    }

    private void setupInputListeners() {
        saveButton.setOnClickListener(v -> viewModel.saveTicket());

        editTitle.addTextChangedListener(new SimpleTextWatcher(s -> viewModel.onFieldChanged(TicketViewModel.FormField.TITLE, s)));
        editDescription.addTextChangedListener(new SimpleTextWatcher(s -> viewModel.onFieldChanged(TicketViewModel.FormField.DESCRIPTION, s)));
        editVersion.addTextChangedListener(new SimpleTextWatcher(s -> viewModel.onFieldChanged(TicketViewModel.FormField.VERSION, s)));

        categoryAutoComplete.setOnItemClickListener((parent, view, position, id) ->
                viewModel.selectedCategory.setValue((Category) parent.getItemAtPosition(position)));
        priorityAutoComplete.setOnItemClickListener((parent, view, position, id) ->
                viewModel.selectedPriority.setValue((Priority) parent.getItemAtPosition(position)));
        softwareAutoComplete.setOnItemClickListener((parent, view, position, id) ->
                viewModel.selectedSoftware.setValue((Software) parent.getItemAtPosition(position)));
    }

    private void observeViewModel() {
        viewModel.ticketFormState.observe(getViewLifecycleOwner(), state -> {
            loadingProgressBar.setVisibility(View.GONE);
            formContent.setVisibility(View.GONE);
            errorTextView.setVisibility(View.GONE);
            submittingProgressBar.setVisibility(View.GONE);

            if (state instanceof TicketFormUIState.Loading) {
                loadingProgressBar.setVisibility(View.VISIBLE);
            }
            else if (state instanceof TicketFormUIState.Error) {
                errorTextView.setVisibility(View.VISIBLE);
                errorTextView.setText(((TicketFormUIState.Error) state).message);
            }
            else if (state instanceof TicketFormUIState.Submitting) {
                formContent.setVisibility(View.VISIBLE);
                setFormEnabled(false);
                saveButton.setEnabled(false);
                submittingProgressBar.setVisibility(View.VISIBLE);
            }
            else if (state instanceof TicketFormUIState.Editing) {
                formContent.setVisibility(View.VISIBLE);
                setFormEnabled(true);
                saveButton.setText(((TicketFormUIState.Editing) state).saveButtonTextResId);

                Boolean isValidNow = viewModel.isFormValid.getValue();
                saveButton.setEnabled(isValidNow != null && isValidNow);
            }
            else if (state instanceof TicketFormUIState.Success) {
                getParentFragmentManager().popBackStack();
            }
        });

        viewModel.titleError.observe(getViewLifecycleOwner(), errorResId ->
                titleInputLayout.setError(errorResId != null ? getString(errorResId) : null));
        viewModel.descriptionError.observe(getViewLifecycleOwner(), errorResId ->
                descriptionInputLayout.setError(errorResId != null ? getString(errorResId) : null));
        viewModel.versionError.observe(getViewLifecycleOwner(), errorResId ->
                versionInputLayout.setError(errorResId != null ? getString(errorResId) : null));

        viewModel.isFormValid.observe(getViewLifecycleOwner(), isValid -> {
            if (viewModel.ticketFormState.getValue() instanceof TicketFormUIState.Editing) {
                saveButton.setEnabled(isValid != null && isValid);
            }
        });

        viewModel.categoryList.observe(getViewLifecycleOwner(), list -> {
            if (getContext() == null || list == null) return;
            ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, list);
            categoryAutoComplete.setAdapter(adapter);
        });

        viewModel.selectedCategory.observe(getViewLifecycleOwner(), selection -> setDropdownSelection(categoryAutoComplete, selection));

        viewModel.priorityList.observe(getViewLifecycleOwner(), list -> {
            if (getContext() == null || list == null) return;
            ArrayAdapter<Priority> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, list);
            priorityAutoComplete.setAdapter(adapter);
        });
        viewModel.selectedPriority.observe(getViewLifecycleOwner(), selection -> setDropdownSelection(priorityAutoComplete, selection));

        viewModel.softwareList.observe(getViewLifecycleOwner(), list -> {
            if (getContext() == null || list == null) return;
            ArrayAdapter<Software> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, list);
            softwareAutoComplete.setAdapter(adapter);
        });
        viewModel.selectedSoftware.observe(getViewLifecycleOwner(), selection -> setDropdownSelection(softwareAutoComplete, selection));

        viewModel.title.observe(getViewLifecycleOwner(), text -> {
            if (!Objects.equals(editTitle.getText().toString(), text)) editTitle.setText(text);
        });

        viewModel.description.observe(getViewLifecycleOwner(), text -> {
            if (!Objects.equals(editDescription.getText().toString(), text)) editDescription.setText(text);
        });

        viewModel.version.observe(getViewLifecycleOwner(), text -> {
            if (!Objects.equals(editVersion.getText().toString(), text)) editVersion.setText(text);
        });

        viewModel.toastMessage.observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null && isResumed()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private <T> void setDropdownSelection(AutoCompleteTextView dropdown, T value) {
        if (value != null) {
            dropdown.setText(value.toString(), false);
        } else {
            dropdown.setText("", false);
        }
    }

    private record SimpleTextWatcher(java.util.function.Consumer<String> onTextChanged) implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            onTextChanged.accept(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }
}
