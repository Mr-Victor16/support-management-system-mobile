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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.models.Category;
import com.example.support_management_system_mobile.models.Priority;
import com.example.support_management_system_mobile.models.Software;
import com.example.support_management_system_mobile.ui.ticket.TicketViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TicketFormFragment extends Fragment {
    private TicketViewModel viewModel;

    private ProgressBar progressBar;
    private NestedScrollView formContent;
    private TextView formHeader, errorMessageTextView;
    private EditText editTitle, editDescription, editVersion;
    private AutoCompleteTextView categoryAutoComplete, priorityAutoComplete, softwareAutoComplete;
    private Button buttonSave;
    private TextInputLayout titleInputLayout, descriptionInputLayout, versionInputLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_form, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TicketViewModel.class);

        long ticketIdValue = -1L;
        if (getArguments() != null) {
            ticketIdValue = getArguments().getLong("ticketId", -1L);
        }

        Long ticketIdForViewModel = ticketIdValue > 0 ? ticketIdValue : null;

        setupInputListeners();
        observeViewModel();

        viewModel.loadForm(ticketIdForViewModel);
    }

    private void initViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        formContent = view.findViewById(R.id.formContent);
        formHeader = view.findViewById(R.id.formHeader);
        editTitle = view.findViewById(R.id.editTitle);
        editDescription = view.findViewById(R.id.editDescription);
        editVersion = view.findViewById(R.id.editVersion);
        categoryAutoComplete = view.findViewById(R.id.categoryAutoComplete);
        priorityAutoComplete = view.findViewById(R.id.priorityAutoComplete);
        softwareAutoComplete = view.findViewById(R.id.softwareAutoComplete);
        buttonSave = view.findViewById(R.id.saveButton);
        titleInputLayout = view.findViewById(R.id.titleInputLayout);
        descriptionInputLayout = view.findViewById(R.id.descriptionInputLayout);
        versionInputLayout = view.findViewById(R.id.versionInputLayout);
        errorMessageTextView = view.findViewById(R.id.errorMessageTextView);
    }

    private void setupInputListeners() {
        buttonSave.setOnClickListener(v -> viewModel.saveTicket());

        editTitle.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                viewModel.onFieldTouched(TicketViewModel.FormField.TITLE);
            }
        });

        editDescription.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                viewModel.onFieldTouched(TicketViewModel.FormField.DESCRIPTION);
            }
        });

        editVersion.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                viewModel.onFieldTouched(TicketViewModel.FormField.VERSION);
            }
        });

        editTitle.addTextChangedListener(new SimpleTextWatcher(s -> viewModel.ticketTitle.setValue(s)));
        editDescription.addTextChangedListener(new SimpleTextWatcher(s -> viewModel.ticketDescription.setValue(s)));
        editVersion.addTextChangedListener(new SimpleTextWatcher(s -> viewModel.ticketVersion.setValue(s)));

        categoryAutoComplete.setOnItemClickListener((parent, view, position, id) ->
                viewModel.selectedCategory.setValue((Category) parent.getItemAtPosition(position)));
        priorityAutoComplete.setOnItemClickListener((parent, view, position, id) ->
                viewModel.selectedPriority.setValue((Priority) parent.getItemAtPosition(position)));
        softwareAutoComplete.setOnItemClickListener((parent, view, position, id) ->
                viewModel.selectedSoftware.setValue((Software) parent.getItemAtPosition(position)));
    }

    private void observeViewModel() {
        viewModel.formState.observe(getViewLifecycleOwner(), state -> {
            boolean isLoading = state instanceof TicketFormUIState.Loading || state instanceof TicketFormUIState.Submitting;
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            formContent.setVisibility(state instanceof TicketFormUIState.Editing ? View.VISIBLE : View.GONE);
            errorMessageTextView.setVisibility(state instanceof TicketFormUIState.Error ? View.VISIBLE : View.GONE);

            if (state instanceof TicketFormUIState.Editing) {
                formHeader.setText(((TicketFormUIState.Editing) state).headerTextResId);
                buttonSave.setText(((TicketFormUIState.Editing) state).saveButtonTextResId);
            } else if (state instanceof TicketFormUIState.Success) {
                getParentFragmentManager().popBackStack();
            } else if (state instanceof TicketFormUIState.Error) {
                errorMessageTextView.setText(((TicketFormUIState.Error) state).messageTextResId);
            }
        });

        viewModel.validationState.observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            titleInputLayout.setError(state.titleError != null ? getString(state.titleError) : null);
            descriptionInputLayout.setError(state.descriptionError != null ? getString(state.descriptionError) : null);
            versionInputLayout.setError(state.versionError != null ? getString(state.versionError) : null);

            buttonSave.setEnabled(state.isSaveButtonEnabled);
        });

        viewModel.categories.observe(getViewLifecycleOwner(), categories -> {
            if (getContext() == null || categories == null) return;

            ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, categories);
            categoryAutoComplete.setAdapter(adapter);
        });

        viewModel.priorities.observe(getViewLifecycleOwner(), priorities -> {
            if (getContext() == null || priorities == null) return;

            ArrayAdapter<Priority> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, priorities);
            priorityAutoComplete.setAdapter(adapter);
        });

        viewModel.software.observe(getViewLifecycleOwner(), softwareList -> {
            if (getContext() == null || softwareList == null) return;

            ArrayAdapter<Software> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, softwareList);
            softwareAutoComplete.setAdapter(adapter);
        });

        viewModel.selectedCategory.observe(getViewLifecycleOwner(), selection ->
                setAutocompleteSelection(categoryAutoComplete, selection));
        viewModel.selectedPriority.observe(getViewLifecycleOwner(), selection ->
                setAutocompleteSelection(priorityAutoComplete, selection));
        viewModel.selectedSoftware.observe(getViewLifecycleOwner(), selection ->
                setAutocompleteSelection(softwareAutoComplete, selection));

        viewModel.ticketTitle.observe(getViewLifecycleOwner(), title -> {
            if (!Objects.equals(editTitle.getText().toString(), title)) {
                editTitle.setText(title);
            }
        });

        viewModel.ticketDescription.observe(getViewLifecycleOwner(), description -> {
            if (!Objects.equals(editDescription.getText().toString(), description)) {
                editDescription.setText(description);
            }
        });

        viewModel.ticketVersion.observe(getViewLifecycleOwner(), version -> {
            if (!Objects.equals(editVersion.getText().toString(), version)) {
                editVersion.setText(version);
            }
        });
    }

    private <T> void setAutocompleteSelection(AutoCompleteTextView autoComplete, T value) {
        if (autoComplete.getAdapter() == null) return;

        String newText = (value != null) ? value.toString() : "";
        if (!autoComplete.getText().toString().equals(newText)) {
            autoComplete.setText(newText, false);
        }
    }

    private static class SimpleTextWatcher implements TextWatcher {
        private final java.util.function.Consumer<String> onTextChanged;

        public SimpleTextWatcher(java.util.function.Consumer<String> onTextChanged) {
            this.onTextChanged = onTextChanged;
        }

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
