package com.example.support_management_system_mobile.ui.ticket;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.models.Category;
import com.example.support_management_system_mobile.models.Priority;
import com.example.support_management_system_mobile.models.Software;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TicketFormFragment extends Fragment {
    private TicketViewModel viewModel;

    private ProgressBar progressBar;
    private ScrollView formContent;
    private TextView formHeader, errorMessageTextView;
    private EditText editTitle, editDescription, editVersion;
    private Spinner spinnerCategory, spinnerPriority, spinnerSoftware;
    private Button btnSave;
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
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerPriority = view.findViewById(R.id.spinnerPriority);
        spinnerSoftware = view.findViewById(R.id.spinnerSoftware);
        btnSave = view.findViewById(R.id.btnSave);
        titleInputLayout = view.findViewById(R.id.titleInputLayout);
        descriptionInputLayout = view.findViewById(R.id.descriptionInputLayout);
        versionInputLayout = view.findViewById(R.id.versionInputLayout);
        errorMessageTextView = view.findViewById(R.id.errorMessageTextView);
    }

    private void setupInputListeners() {
        btnSave.setOnClickListener(v -> viewModel.saveTicket());

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

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.selectedCategory.setValue((Category) parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.selectedCategory.setValue(null);
            }
        });

        spinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.selectedPriority.setValue((Priority) parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.selectedPriority.setValue(null);
            }
        });

        spinnerSoftware.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.selectedSoftware.setValue((Software) parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.selectedSoftware.setValue(null);
            }
        });
    }

    private void observeViewModel() {
        viewModel.formState.observe(getViewLifecycleOwner(), state -> {
            boolean isLoading = state instanceof TicketFormUIState.Loading || state instanceof TicketFormUIState.Submitting;
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            formContent.setVisibility(state instanceof TicketFormUIState.Editing ? View.VISIBLE : View.GONE);
            errorMessageTextView.setVisibility(state instanceof TicketFormUIState.Error ? View.VISIBLE : View.GONE);

            if (state instanceof TicketFormUIState.Editing) {
                formHeader.setText(((TicketFormUIState.Editing) state).headerTextResId);
                btnSave.setText(((TicketFormUIState.Editing) state).saveButtonTextResId);
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

            btnSave.setEnabled(state.isSaveButtonEnabled);
        });

        viewModel.categories.observe(getViewLifecycleOwner(), categories -> {
            if (getContext() == null || categories == null) return;
            ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);
        });

        viewModel.priorities.observe(getViewLifecycleOwner(), priorities -> {
            if (getContext() == null || priorities == null) return;
            ArrayAdapter<Priority> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, priorities);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPriority.setAdapter(adapter);
        });

        viewModel.software.observe(getViewLifecycleOwner(), softwareList -> {
            if (getContext() == null || softwareList == null) return;
            ArrayAdapter<Software> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, softwareList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSoftware.setAdapter(adapter);
        });

        viewModel.selectedCategory.observe(getViewLifecycleOwner(), selection -> setSpinnerSelection(spinnerCategory, selection));
        viewModel.selectedPriority.observe(getViewLifecycleOwner(), selection -> setSpinnerSelection(spinnerPriority, selection));
        viewModel.selectedSoftware.observe(getViewLifecycleOwner(), selection -> setSpinnerSelection(spinnerSoftware, selection));

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

    private <T> void setSpinnerSelection(Spinner spinner, T value) {
        if (value == null || spinner.getAdapter() == null) {
            return;
        }

        if (spinner.getAdapter() instanceof ArrayAdapter) {
            @SuppressWarnings("unchecked")
            ArrayAdapter<T> adapter = (ArrayAdapter<T>) spinner.getAdapter();

            int position = adapter.getPosition(value);

            if (position >= 0) {
                spinner.setSelection(position);
            }
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
